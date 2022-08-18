package com.speakeasy.request.perfume;


import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

import static java.lang.Math.max;
import static java.lang.Math.min;

//페이지서치를 위한 클래스 구현
@Getter //getter, setter 사용
@Setter
public class PerfumeSearch {

    private static final int MAX_SIZE = 2000;


    private Integer page;
    private Integer size;
    private Integer gender;
    private Integer goePoints;
    private Integer loePoints;
    private List<Long> topNotes;
//    private List<Long> heartNotes;
//    private List<Long> baseNotes;

    private List<String> brand;
    private List<String> searchKey;


    @Builder
    public PerfumeSearch(Integer page, Integer size, Integer goePoints, Integer loePoints, List<Long> topNotes, List<String> brand, List<String> searchKey) {
        this.page = page == null ? 1 : page;
        this.size = size == null ? 10 :size;
        this.goePoints = goePoints;
        this.loePoints = loePoints;
        this.topNotes = topNotes;
        this.brand = brand;
        this.searchKey = searchKey;
    }

    public long getOffset(){
        //페이지 0이 요청될 시 1페이지 반환
        return (long) (max(1,page) - 1) * min(size,MAX_SIZE);
    }

}