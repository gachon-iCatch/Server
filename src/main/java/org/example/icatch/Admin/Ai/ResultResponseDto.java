package org.example.icatch.Admin.Ai;

public class ResultResponseDto {
    private String name;
    private String url;

    public ResultResponseDto(String name, String url) {
        this.name = name;
        this.url = url;
    }

    public String getName() {
        return name;
    }

    public String getUrl() {
        return url;
    }
}
