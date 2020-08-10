package com.google.pso.util;

import com.google.pso.Document;
import com.google.pso.Page;
import org.apache.beam.sdk.values.KV;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.pdfbox.cos.COSObject;
import org.apache.pdfbox.io.MemoryUsageSetting;
import org.apache.pdfbox.pdmodel.DefaultResourceCache;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.graphics.PDXObject;
import org.apache.pdfbox.rendering.ImageType;
import org.apache.pdfbox.rendering.PDFRenderer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;

public class PdfProcessor implements Serializable {
    private final Logger LOG = LoggerFactory.getLogger(PdfProcessor.class);
    private String bucket;
    private String pathPrefix;

    public PdfProcessor(String bucket, String pathPrefix) {
        this.bucket = bucket;
        this.pathPrefix = pathPrefix;
    }

    public KV<Document, Iterable<Page>> process(String bucket, String filename) {
        String filepath = "gs://" + bucket + '/' + filename;
        String[] parts = filename.split("/");
        String name = parts[parts.length - 1];
        String docHash = "";
        Integer pageCount = 0;
        List<Page> pages = null;

        if (isPdf(filename)) {
            try {
                LOG.info(String.format("Processing document %s", filepath));
                byte[] blob = GCS.download(bucket, filename);
                InputStream inputStream = new ByteArrayInputStream(blob);

                PDDocument pdf = PDDocument.load(inputStream, MemoryUsageSetting.setupMixed(
                        200L * 1024 * 1024));
                pdf.setResourceCache(new DefaultResourceCache() {
                    @Override
                    public void put(COSObject indirect, PDXObject xobject) throws IOException {
                        // Do nothing to avoid memory consumption
                        // See https://pdfbox.apache.org/2.0/faq.html#im-getting-an-outofmemoryerror-what-can-i-do
                        // for more explanation
                    }
                });
                docHash = DigestUtils.md5Hex(blob);
                pageCount = pdf.getPages().getCount();
                pages = processDocument(pdf, docHash, filepath);

                pdf.close();
            } catch (IOException e) {
                LOG.error(String.format("Failed to process file %s", filepath));
            }
        } else {
            LOG.info(String.format("Document %s is not a PDF file", filepath));
        }
        Document document = Document.newBuilder().
                setDocHash(docHash).
                setDocName(name).
                setNumPages(pageCount).
                setDocPath(filepath).
                build();

        return KV.of(document, pages);
    }

    private static boolean isPdf(String filename) {
        return filename.toLowerCase().endsWith(".pdf");
    }

    private List<Page> processDocument(PDDocument pdf, String docHash, String docName) {
        List<Page> pages = new ArrayList<Page>();
        PDFRenderer renderer = new PDFRenderer(pdf);
        int nbPages = pdf.getNumberOfPages();
        IntStream.range(0, nbPages).forEach(i -> {
            try {
                BufferedImage image = renderer.renderImageWithDPI(i, 200, ImageType.RGB);
                ImageProcessor.imageToBytes(image).ifPresent(buffer -> {
                    String pageHash = DigestUtils.md5Hex(buffer);
                    String filename = this.pathPrefix + "/" + docHash + "/" + pageHash;
                    GCS.upload(this.bucket, filename, buffer, "jpg");
                    Page page = Page.newBuilder().
                            setPageIndex(i + 1).
                            setPageMd5(pageHash).
                            setFileId(docHash).
                            build();
                    pages.add(page);
                });
            } catch (Throwable e) {
                LOG.error("Cannot render the page " + i + " of document " + docName, e);
            }
        });
        return pages;
    }
}
