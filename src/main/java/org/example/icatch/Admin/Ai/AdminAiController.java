package org.example.icatch.Admin.Ai;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.beans.factory.annotation.Autowired;


import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


@RestController
@RequestMapping("/api/admin/ai")
public class AdminAiController {

    @GetMapping("/results")
    public List<ResultResponseDto> getAiResultImages(@RequestParam String modelDir) {
        return List.of(
                new ResultResponseDto("F1 Curve", "/api/results/" + modelDir + "/F1_curve.png"),
                new ResultResponseDto("Precision Curve", "/api/results/" + modelDir + "/P_curve.png"),
                new ResultResponseDto("Recall Curve", "/api/results/" + modelDir + "/R_curve.png"),
                new ResultResponseDto("PR Curve", "/api/results/" + modelDir + "/PR_curve.png"),
                new ResultResponseDto("Labels Heatmap", "/api/results/" + modelDir + "/labels.jpg"),
                new ResultResponseDto("Results Summary", "/api/results/" + modelDir + "/results.png")
        );
    }




    @GetMapping("/models")
    public List<String> getAvailableModelFolders() {
        File detectRoot = new File("/home/t25104/v0.1src/ai/runs/detect");
        File[] directories = detectRoot.listFiles(File::isDirectory);
        if (directories == null) return List.of();

        return Arrays.stream(directories)
                .map(File::getName)
                .collect(Collectors.toList());
    }

    @Autowired
    private TrainingStatusService trainingStatusService;
    @Autowired
    private TrainProcessManager processManager;

    @PostMapping("/train/start")
    public ResponseEntity<Map<String, Object>> startTraining(@RequestBody TrainRequestDto request) {
        Map<String, Object> response = new HashMap<>();
        String runPath = "/home/t25104/v0.1src/ai/runs/detect/" + request.getModelname();
        File modelDir = new File(runPath);

        if (modelDir.exists()) {
            response.put("status", 409); // Conflict
            response.put("training", "false");
            response.put("message", "같은 이름의 학습 결과가 이미 존재합니다.");
            return ResponseEntity.status(HttpStatus.CONFLICT).body(response);
        }

        try {
            new FileWriter("training-log.txt", false).close();

            trainingStatusService.setStatus(TrainingStatus.TRAINING);

            String pythonPath = "python3";
            String scriptPath = "/home/t25104/v0.1src/ai/train.py";

            List<String> cmd = List.of(
                    pythonPath,
                    scriptPath,
                    "--model", request.getModel(),
                    "--imgsz", request.getImgs(),
                    "--epochs", request.getEpochs(),
                    "--batchsize", request.getBatchsize(),
                    "--modelname", request.getModelname()
            );

            ProcessBuilder pb = new ProcessBuilder(cmd);
            pb.redirectErrorStream(true);
            Process process = pb.start();
            processManager.setProcess(process);

            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            FileWriter logWriter = new FileWriter("training-log.txt", true); // append mode

            String line;
            while ((line = reader.readLine()) != null) {
                System.out.println("[PYTHON] " + line);
                logWriter.write(line + "\n");
                        logWriter.flush();
            }
            int exitCode = process.waitFor();

            if (exitCode == 0) {
                trainingStatusService.setStatus(TrainingStatus.COMPLETED);
                response.put("status", 200);
                response.put("training", "true");
            } else {
                trainingStatusService.setStatus(TrainingStatus.IDLE);
                response.put("status", 200);
                response.put("training", "false");
            }

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            trainingStatusService.setStatus(TrainingStatus.IDLE);
            response.put("status", 500);
            response.put("training", "false");
            response.put("error", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    @GetMapping("/train/status")
    public ResponseEntity<Map<String, String>> getTrainingStatus() {
        return ResponseEntity.ok(Map.of("status", trainingStatusService.getStatus().name()));
    }

    @GetMapping("/train/logs")
    public ResponseEntity<List<String>> getLatestLogs() throws IOException {
        List<String> lines = Files.readAllLines(Paths.get("/home/t25104/Server/training-log.txt"));
        int from = Math.max(0, lines.size() - 20); // 최근 20줄만 반환
        return ResponseEntity.ok(lines.subList(from, lines.size()));
    }

    @PostMapping("/train/stop")
    public ResponseEntity<Map<String, String>> stopTraining() {
        processManager.stopProcess();
        trainingStatusService.setStatus(TrainingStatus.IDLE);  // 상태도 변경
        return ResponseEntity.ok(Map.of("status", "STOPPED"));
    }

    @GetMapping("/models/{modelName}.pt")
    public ResponseEntity<byte[]> downloadModel(@PathVariable String modelName) {
        String modelPath = "/home/t25104/v0.1src/ai/runs/detect/" + modelName + "/weights/best.pt";
        File file = new File(modelPath);

        if (!file.exists()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(("파일 없음: " + modelName).getBytes());
        }

        try {
            byte[] fileContent = Files.readAllBytes(file.toPath());

            return ResponseEntity.ok()
                    .header("Content-Disposition", "attachment; filename=\"" + modelName + ".pt\"")
                    .header("Content-Type", "application/octet-stream")
                    .body(fileContent);

        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(("파일 읽기 오류: " + e.getMessage()).getBytes());
        }
    }

    @DeleteMapping("/models/{modelName}")
    public ResponseEntity<Map<String, Object>> deleteModel(@PathVariable String modelName) {
        String modelPath = "/home/t25104/v0.1src/ai/runs/detect/" + modelName;
        File modelDir = new File(modelPath);

        Map<String, Object> result = new HashMap<>();

        if (!modelDir.exists()) {
            result.put("status", 404);
            result.put("message", "모델 디렉토리가 존재하지 않습니다.");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(result);
        }

        try {
            deleteDirectoryRecursively(modelDir);
            result.put("status", 200);
            result.put("message", "삭제 완료");
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            result.put("status", 500);
            result.put("message", "삭제 실패: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(result);
        }
    }

    private void deleteDirectoryRecursively(File dir) throws IOException {
        if (dir.isDirectory()) {
            for (File file : dir.listFiles()) {
                deleteDirectoryRecursively(file);
            }
        }
        if (!dir.delete()) {
            throw new IOException("파일 삭제 실패: " + dir.getAbsolutePath());
        }
    }



}
