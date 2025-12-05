package org.example.springboot_hd.entity;


import lombok.Data;

import java.util.ArrayList;
import java.util.List;
@Data
public class Menu {

    private long id;
    private String menuName;
    private String menuPath;
    private long parentId;
    private String icon;
    private List<Menu> children;

}
