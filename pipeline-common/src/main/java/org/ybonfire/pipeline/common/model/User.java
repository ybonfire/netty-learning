package org.ybonfire.pipeline.common.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * 这里添加类的注释【强制】
 *
 * @author Bo.Yuan5
 * @date 2022-05-26 19:07
 */
@NoArgsConstructor
@AllArgsConstructor
@Data
public class User implements Serializable {
    private String name;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
