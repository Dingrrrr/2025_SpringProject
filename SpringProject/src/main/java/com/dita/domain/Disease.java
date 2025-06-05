package com.dita.domain;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Disease {

    @Id
    private String code; // 예: "A00", "J10.1"
    
    private String name; // 예: "콜레라", "인플루엔자 바이러스 감염"

    // 필요 시 category 등 추가 가능
}

