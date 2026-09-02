package com.systemdesign.common;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.MDC;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.UUID;

/**
 * Correlation ID filter — her HTTP request'e benzersiz bir izleme kimliği atar.
 * <p>
 * Gelen {@code X-Correlation-Id} header'ı varsa onu kullanır; yoksa UUID üretir.
 * ID hem response header'a yazılır hem de SLF4J {@code MDC}'ye konur;
 * böylece tüm log satırları aynı request'i takip edebilir.
 */
@Component
@Order(Ordered.HIGHEST_PRECEDENCE)
public class CorrelationIdFilter extends OncePerRequestFilter {

    /** Request/response'ta taşınan correlation id header adı. */
    public static final String CORRELATION_ID_HEADER = "X-Correlation-Id";
    /** Log MDC'de kullanılan key; log pattern'de {@code %X{correlationId}} ile okunur. */
    public static final String MDC_KEY = "correlationId";

    /**
     * Her request için correlation id'yi okur/üretir, MDC'ye yazar ve filter chain'i devam ettirir.
     * <p>
     * {@code finally} bloğunda MDC temizlenir — thread pool'da sonraki request'e sızmayı önler.
     */
    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain) throws ServletException, IOException {
        // Correlation id: gelen header varsa reuse et, yoksa yeni UUID üret
        String correlationId = request.getHeader(CORRELATION_ID_HEADER);
        if (correlationId == null || correlationId.isBlank()) {
            correlationId = UUID.randomUUID().toString();
        }
        // MDC'ye yaz — bu request boyunca tüm log satırları bu id'yi taşır
        MDC.put(MDC_KEY, correlationId);
        // Client'ın downstream servislere iletebilmesi için response header'a da ekle
        response.setHeader(CORRELATION_ID_HEADER, correlationId);
        try {
            filterChain.doFilter(request, response);
        } finally {
            // Thread pool reuse: MDC'yi temizle, bir sonraki request'e sızmasın
            MDC.remove(MDC_KEY);
        }
    }
}
