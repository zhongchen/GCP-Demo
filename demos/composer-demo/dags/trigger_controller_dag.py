"""
Example usage of the TriggerDagRunOperator. This example holds 2 DAGs:
1. 1st DAG (example_trigger_controller_dag) holds a TriggerDagRunOperator, which will trigger the 2nd DAG
2. 2nd DAG (example_trigger_target_dag) which will be triggered by the TriggerDagRunOperator in the 1st DAG
"""
from airflow import DAG
from airflow.operators.dagrun_operator import TriggerDagRunOperator
from airflow.utils.dates import days_ago

dag = DAG(
    dag_id="example_trigger_controller_dag",
    default_args={"owner": "airflow", "start_date": days_ago(2)},
    schedule_interval="@once",
    tags=['example']
)

trigger = TriggerDagRunOperator(
    task_id="test_trigger_dagrun",
    trigger_dag_id="example_trigger_target_dag",  # Ensure this equals the dag_id of the DAG to trigger
    conf={"message": "Hello World"},
    dag=dag,
)