package org.example.icatch.Admin.Ai;

import org.springframework.stereotype.Service;

@Service
public class TrainingStatusService {
    private TrainingStatus status = TrainingStatus.IDLE;

    public synchronized void setStatus(TrainingStatus status) {
        this.status = status;
    }

    public synchronized TrainingStatus getStatus() {
        return status;
    }
}
