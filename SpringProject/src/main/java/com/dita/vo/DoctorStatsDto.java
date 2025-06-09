package com.dita.vo;

public class DoctorStatsDto {

    private String doctorName;
    private Long visitCount;

    // ✅ 기본 생성자 (반드시 있어야 함)
    public DoctorStatsDto() {
    }

    // ✅ 필드 초기화 생성자
    public DoctorStatsDto(String doctorName, Long visitCount) {
        this.doctorName = doctorName;
        this.visitCount = visitCount;
    }

    // ✅ getter
    public String getDoctorName() {
        return doctorName;
    }

    public Long getVisitCount() {
        return visitCount;
    }

    // (선택) setter 도 추가 가능
    public void setDoctorName(String doctorName) {
        this.doctorName = doctorName;
    }

    public void setVisitCount(Long visitCount) {
        this.visitCount = visitCount;
    }
}
