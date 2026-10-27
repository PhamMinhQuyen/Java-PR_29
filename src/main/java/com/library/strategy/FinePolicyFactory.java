package com.library.strategy;

import com.library.exception.BusinessException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Factory / Registry quản lý các FinePolicy theo nguyên lý OCP.
 * Tự động thu thập tất cả các Bean triển khai FinePolicy thông qua Spring DI.
 * Khi lập trình viên tạo thêm chính sách mới (ví dụ VipFinePolicy, WeekendFreeFinePolicy),
 * chỉ cần tạo class có @Component, Factory sẽ tự động nhận diện mà không cần sửa code ở đây hay ở LoanService.
 */
@Component
public class FinePolicyFactory {

    private final Map<String, FinePolicy> policies = new ConcurrentHashMap<>();
    private final String defaultPolicyName;

    public FinePolicyFactory(List<FinePolicy> policyList,
                             @Value("${library.fine.default-policy:DAILY}") String defaultPolicyName) {
        if (policyList != null) {
            for (FinePolicy policy : policyList) {
                registerPolicy(policy);
            }
        }
        this.defaultPolicyName = defaultPolicyName != null ? defaultPolicyName.toUpperCase() : DailyFinePolicy.POLICY_NAME;
    }

    /**
     * Đăng ký một chính sách phạt mới vào hệ thống (hỗ trợ mở rộng runtime / testing).
     */
    public void registerPolicy(FinePolicy policy) {
        if (policy != null && policy.getPolicyName() != null) {
            policies.put(policy.getPolicyName().toUpperCase(), policy);
        }
    }

    /**
     * Lấy chính sách phạt theo tên. Nếu không truyền tên hoặc tên rỗng, sử dụng chính sách mặc định.
     *
     * @param policyName Tên chính sách (DAILY, FIXED, PROGRESSIVE,...)
     * @return Đối tượng FinePolicy tương ứng
     * @throws BusinessException nếu không tìm thấy chính sách được yêu cầu
     */
    public FinePolicy getPolicy(String policyName) {
        if (policyName == null || policyName.trim().isEmpty()) {
            return getDefaultPolicy();
        }

        FinePolicy policy = policies.get(policyName.trim().toUpperCase());
        if (policy == null) {
            throw new BusinessException("Chính sách tính phí phạt không hợp lệ: '" + policyName
                    + "'. Các chính sách khả dụng: " + policies.keySet());
        }
        return policy;
    }

    /**
     * Lấy chính sách mặc định được cấu hình trong application.yml.
     */
    public FinePolicy getDefaultPolicy() {
        FinePolicy defaultPolicy = policies.get(defaultPolicyName);
        if (defaultPolicy == null) {
            // Fallback an toàn nếu chưa đăng ký default
            return policies.values().stream().findFirst()
                    .orElseThrow(() -> new BusinessException("Không có chính sách tính phí phạt nào được đăng ký trong hệ thống."));
        }
        return defaultPolicy;
    }

    public Map<String, FinePolicy> getAvailablePolicies() {
        return Collections.unmodifiableMap(policies);
    }
}
