package org.example.icatch.Admin.Ai;

import org.springframework.stereotype.Component;

@Component
public class TrainProcessManager {
    private Process currentProcess;

    public void setProcess(Process process) {
        this.currentProcess = process;
    }

    public void stopProcess() {
        if (currentProcess != null && currentProcess.isAlive()) {
            currentProcess.destroy();  // 또는 destroyForcibly();
        }
    }
}

