package org.apache;

public class ProducerConsumer {
    public static void main(String[] args) {
        System.out.println("Start the producer consumer");
        BlockingQueue queue = new BlockingQueue(5);
        Producer producer = new Producer(queue);
        Consumer consumer = new Consumer(queue);


        Thread t1 = new Thread(producer);
        Thread t2 = new Thread(consumer);

        t1.start();
        t2.start();
    }
}

class Producer implements Runnable {
    BlockingQueue queue;

    public Producer(BlockingQueue queue) {
        this.queue = queue;
    }

    @Override
    public void run() {
        for (int i = 0; i < 20; ++i) {
            try {
                Thread.sleep(100);
                queue.put(i);
                System.out.println("Putting element " + i);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}

class Consumer implements Runnable {
    BlockingQueue queue;

    public Consumer(BlockingQueue queue) {
        this.queue = queue;
    }

    @Override
    public void run() {
        for (int i = 0; i < 10; ++i) {
            try {
                Thread.sleep(500);
                int elem = queue.get();
                System.out.println("Getting element " + elem);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}

/*
The key thing is defining the shared object first. The object will be shared between threads.
Different thread will call wait to release its lock on the object and call notify to wake up other
threads so that they can continue processing.
 */
class BlockingQueue {
    private int in;
    private int out;
    private int count;
    private int[] buffer;
    private int capacity;

    public BlockingQueue(int size) {
        in = 0;
        out = 0;
        buffer = new int[size];
        count = 0;
        capacity = size;
    }

    // The synchronized method blocks the object, which means only one thread can enter the
    // the synchronized methods. 
    synchronized void put(int num) {
        while (count == capacity) {
            try {
                wait();
            } catch (InterruptedException e) {
                System.out.println(e);
            }
        }

        buffer[in] = num;
        in = (in + 1) % capacity;
        count += 1;
        notifyAll();
    }

    synchronized int get() {
        while (count == 0) {
            try {
                wait();
            } catch (InterruptedException e) {
                System.out.println(e);
            }
        }
        int elem = buffer[out];
        out = (out + 1) % capacity;
        count -= 1;
        notifyAll();
        return elem;
    }

}
