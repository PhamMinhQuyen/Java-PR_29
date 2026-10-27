package com.library.strategy;

import java.math.BigDecimal;

/**
 * Interface Strategy định nghĩa hợp đồng tính phí phạt theo nguyên lý OCP và LSP.
 * - OCP: Khi có chính sách mới (ví dụ: lũy tiến theo số tuần, miễn giảm cho VIP), chỉ cần tạo class mới implements interface này.
 * - LSP: Mọi implementation đều phải đảm bảo tính nhất quán: overdueDays <= 0 thì phí phạt = 0, kết quả không âm.
 */
public interface FinePolicy {

    /**
     * Tên định danh của chính sách phạt (ví dụ: DAILY, FIXED, PROGRESSIVE).
     *
     * @return Mã định danh chính sách
     */
    String getPolicyName();

    /**
     * Tính toán số tiền phạt tương ứng với số ngày quá hạn.
     *
     * @param overdueDays Số ngày trễ hạn (>= 0)
     * @return Số tiền phạt tương ứng (đơn vị tiền tệ, không âm)
     */
    BigDecimal calculateFine(long overdueDays);
}
