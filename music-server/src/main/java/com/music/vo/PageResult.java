package com.music.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

/**
 * 通用分页结果：列表 + 总条数 + 当前页 + 每页大小
 */
@Data
@Schema(description = "分页结果")
public class PageResult<T> {
    @Schema(description = "当前页数据列表")
    private List<T> list;
    @Schema(description = "总条数")
    private long total;
    @Schema(description = "当前页码（从 1 开始）")
    private int page;
    @Schema(description = "每页条数")
    private int size;

    public static <T> PageResult<T> of(List<T> list, long total, int page, int size) {
        PageResult<T> r = new PageResult<>();

        r.setList(list);
        r.setTotal(total);
        r.setPage(page);
        r.setSize(size);

        return r;
    }
}
