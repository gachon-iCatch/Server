package org.example.icatch.Admin.Ai;
import org.example.icatch.Admin.Ai.ResultResponseDto;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.File;
import java.util.Arrays;
import java.util.List;
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

}
