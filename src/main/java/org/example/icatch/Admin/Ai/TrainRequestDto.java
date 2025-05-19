package org.example.icatch.Admin.Ai;

public class TrainRequestDto {
    private String model;
    private String imgs;
    private String epochs;
    private String batchsize;
    private String modelname;

    // 꼭 필요한 기본 생성자
    public TrainRequestDto() {}

    // 🔥 여기가 중요: Getter 메서드 추가
    public String getModel() {
        return model;
    }

    public String getImgs() {
        return imgs;
    }

    public String getEpochs() {
        return epochs;
    }

    public String getBatchsize() {
        return batchsize;
    }

    public String getModelname() {
        return modelname;
    }

    // Setter도 있으면 좋음 (Jackson 역직렬화에 필요)
    public void setModel(String model) {
        this.model = model;
    }

    public void setImgs(String imgs) {
        this.imgs = imgs;
    }

    public void setEpochs(String epochs) {
        this.epochs = epochs;
    }

    public void setBatchsize(String batchsize) {
        this.batchsize = batchsize;
    }

    public void setModelname(String modelname) {
        this.modelname = modelname;
    }
}