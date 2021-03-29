package com.yangyang.unmanneddrone.Body;
//测量记录
public class MeasurementBody {
    /**
     * 测量名称
     */
    private String measurementName;
    /**
     * 平均流速
     */
    private String averageVelocity;
    /**
     * 测量时间
     */
    private String measureTime;
    /**
     * 流量
     */
    private String flow;

    public String getMeasurementName() {
        return measurementName;
    }

    public void setMeasurementName(String measurementName) {
        this.measurementName = measurementName;
    }

    public String getAverageVelocity() {
        return averageVelocity;
    }

    public void setAverageVelocity(String averageVelocity) {
        this.averageVelocity = averageVelocity;
    }

    public String getMeasureTime() {
        return measureTime;
    }

    public void setMeasureTime(String measureTime) {
        this.measureTime = measureTime;
    }

    public String getFlow() {
        return flow;
    }

    public void setFlow(String flow) {
        this.flow = flow;
    }

    @Override
    public String toString() {
        return "MeasurementBody{" +
                "measurement_name='" + measurementName + '\'' +
                ", average_velocity='" + averageVelocity + '\'' +
                ", measure_time='" + measureTime + '\'' +
                ", flow='" + flow + '\'' +
                '}';
    }
}
