package com.wf.ew.common.paperview;

import java.lang.annotation.*;

@Target({ElementType.PARAMETER, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface PaperView {
    /**
     * 描述
     */
    String description()  default "";
}