package com.app.common.infrastructure.request;

import com.app.common.infrastructure.constants.PagableConstant;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 *
 * @author inuHa
 */
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class FilterRequest {

    protected int page = PagableConstant.PAGE;

    protected int size = PagableConstant.SIZE;

    public static int[] getOffset(int page, int size) {
        int offset = (page - 1) * size;
        return new int[] {
                offset + 1,
                offset + size
        };
    }

}
