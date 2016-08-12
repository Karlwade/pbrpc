package dos.executor.client;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import dos.executor.ExecutorDescriptor.CallableTask;
import dos.executor.ExecutorDescriptor.CallableTaskResult;

public class ClientProcessContext implements Future<CallableTaskResult> {

    private Object tx = new Object();
    private CallableTask task;
    private CallableTaskResult result;
    private boolean completed;
    public CallableTask getTask() {
        return task;
    }
    public void setTask(CallableTask task) {
        this.task = task;
    }
    public CallableTaskResult getResult() {
        return result;
    }
    public void setResult(CallableTaskResult result) {
        this.result = result;
    }
    
    @Override
    public boolean cancel(boolean mayInterruptIfRunning) {
        // TODO Auto-generated method stub
        return false;
    }
    @Override
    public boolean isCancelled() {
        // TODO Auto-generated method stub
        return false;
    }
    @Override
    public boolean isDone() {
        return completed;
    }
    
    @Override
    public CallableTaskResult get() throws InterruptedException, ExecutionException {
        synchronized (tx) {
            while (!completed) {
                tx.wait(1000);
            }
        }
        return result;
    }
    
    
    @Override
    public CallableTaskResult get(long timeout, TimeUnit unit)
            throws InterruptedException, ExecutionException, TimeoutException {
        
        return null;
    }
    
}
