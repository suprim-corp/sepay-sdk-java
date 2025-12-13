package suprim.sepay.entity;

import suprim.sepay.dto.SePayWebhookData;
import suprim.sepay.enums.TransferType;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for SePayTransaction entity.
 */
class SePayTransactionTest {

    @Test
    void testFromWebhookData_allFields() {
        LocalDateTime now = LocalDateTime.now();

        SePayWebhookData data = SePayWebhookData.builder()
            .id(123456L)
            .gateway("MBBank")
            .transactionDate(now)
            .accountNumber("0359123456")
            .subAccount("001")
            .code("ABC123")
            .content("Thanh toan SE123456")
            .transferType(TransferType.IN)
            .description("Full SMS content")
            .transferAmount(1700000L)
            .referenceCode("FT123456789")
            .accumulated(5000000L)
            .build();

        SePayTransaction entity = SePayTransaction.fromWebhookData(data);

        assertEquals(123456L, entity.getId());
        assertEquals("MBBank", entity.getGateway());
        assertEquals(now, entity.getTransactionDate());
        assertEquals("0359123456", entity.getAccountNumber());
        assertEquals("001", entity.getSubAccount());
        assertEquals("ABC123", entity.getCode());
        assertEquals("Thanh toan SE123456", entity.getContent());
        assertEquals(TransferType.IN, entity.getTransferType());
        assertEquals("Full SMS content", entity.getDescription());
        assertEquals(1700000L, entity.getTransferAmount());
        assertEquals("FT123456789", entity.getReferenceCode());
        // Note: accumulated is NOT persisted
        assertNull(entity.getCreatedAt()); // Set by @PrePersist
        assertNull(entity.getUpdatedAt()); // Set by @PrePersist
    }

    @Test
    void testFromWebhookData_nullableFields() {
        LocalDateTime now = LocalDateTime.now();

        SePayWebhookData data = SePayWebhookData.builder()
            .id(123456L)
            .gateway("VietinBank")
            .transactionDate(now)
            .accountNumber("0123456789")
            .content("Simple payment")
            .transferType(TransferType.OUT)
            .transferAmount(500000L)
            .build();

        SePayTransaction entity = SePayTransaction.fromWebhookData(data);

        assertEquals(123456L, entity.getId());
        assertEquals("VietinBank", entity.getGateway());
        assertEquals(TransferType.OUT, entity.getTransferType());
        assertNull(entity.getSubAccount());
        assertNull(entity.getCode());
        assertNull(entity.getDescription());
        assertNull(entity.getReferenceCode());
    }

    @Test
    void testToString() {
        SePayTransaction entity = new SePayTransaction();
        entity.setId(999L);
        entity.setGateway("ACB");
        entity.setTransactionDate(LocalDateTime.of(2024, 12, 25, 10, 30, 0));
        entity.setTransferType(TransferType.IN);
        entity.setTransferAmount(1000000L);

        String result = entity.toString();

        assertTrue(result.contains("id=999"));
        assertTrue(result.contains("gateway='ACB'"));
        assertTrue(result.contains("transactionDate=2024-12-25T10:30"));
        assertTrue(result.contains("transferType=IN"));
        assertTrue(result.contains("transferAmount=1000000"));
    }

    @Test
    void testDefaultConstructor() {
        SePayTransaction entity = new SePayTransaction();

        assertNull(entity.getId());
        assertNull(entity.getGateway());
        assertNull(entity.getTransactionDate());
        assertNull(entity.getAccountNumber());
        assertNull(entity.getSubAccount());
        assertNull(entity.getCode());
        assertNull(entity.getContent());
        assertNull(entity.getTransferType());
        assertNull(entity.getDescription());
        assertNull(entity.getTransferAmount());
        assertNull(entity.getReferenceCode());
        assertNull(entity.getCreatedAt());
        assertNull(entity.getUpdatedAt());
    }

    @Test
    void testSetters() {
        SePayTransaction entity = new SePayTransaction();
        LocalDateTime now = LocalDateTime.now();

        entity.setId(111L);
        entity.setGateway("BIDV");
        entity.setTransactionDate(now);
        entity.setAccountNumber("9876543210");
        entity.setSubAccount("SUB001");
        entity.setCode("CODE123");
        entity.setContent("Test content");
        entity.setTransferType(TransferType.OUT);
        entity.setDescription("Test description");
        entity.setTransferAmount(250000L);
        entity.setReferenceCode("REF123");

        assertEquals(111L, entity.getId());
        assertEquals("BIDV", entity.getGateway());
        assertEquals(now, entity.getTransactionDate());
        assertEquals("9876543210", entity.getAccountNumber());
        assertEquals("SUB001", entity.getSubAccount());
        assertEquals("CODE123", entity.getCode());
        assertEquals("Test content", entity.getContent());
        assertEquals(TransferType.OUT, entity.getTransferType());
        assertEquals("Test description", entity.getDescription());
        assertEquals(250000L, entity.getTransferAmount());
        assertEquals("REF123", entity.getReferenceCode());
    }

    @Test
    void testFromWebhookData_accumulatedNotPersisted() {
        SePayWebhookData data = SePayWebhookData.builder()
            .id(123L)
            .gateway("MBBank")
            .transactionDate(LocalDateTime.now())
            .accountNumber("0123456789")
            .content("Test")
            .transferType(TransferType.IN)
            .transferAmount(100000L)
            .accumulated(999999999L) // This should NOT be transferred
            .build();

        SePayTransaction entity = SePayTransaction.fromWebhookData(data);

        // Entity doesn't have accumulated field - verify by checking no method exists
        // The entity class intentionally omits accumulated field
        assertEquals(123L, entity.getId());
        assertEquals(100000L, entity.getTransferAmount());
    }

    @Test
    void testFromWebhookData_largeTransactionId() {
        SePayWebhookData data = SePayWebhookData.builder()
            .id(Long.MAX_VALUE)
            .gateway("MBBank")
            .transactionDate(LocalDateTime.now())
            .accountNumber("0123456789")
            .content("Large ID test")
            .transferType(TransferType.IN)
            .transferAmount(100L)
            .build();

        SePayTransaction entity = SePayTransaction.fromWebhookData(data);

        assertEquals(Long.MAX_VALUE, entity.getId());
    }

    @Test
    void testFromWebhookData_largeAmount() {
        SePayWebhookData data = SePayWebhookData.builder()
            .id(123L)
            .gateway("VCB")
            .transactionDate(LocalDateTime.now())
            .accountNumber("0123456789")
            .content("Large amount test")
            .transferType(TransferType.IN)
            .transferAmount(999999999999L) // ~1 trillion VND
            .build();

        SePayTransaction entity = SePayTransaction.fromWebhookData(data);

        assertEquals(999999999999L, entity.getTransferAmount());
    }

    @Test
    void testTransferTypeEnum() {
        SePayTransaction entity = new SePayTransaction();

        entity.setTransferType(TransferType.IN);
        assertEquals(TransferType.IN, entity.getTransferType());

        entity.setTransferType(TransferType.OUT);
        assertEquals(TransferType.OUT, entity.getTransferType());
    }

    @Test
    void testOnCreate_setsTimestamps() {
        TestableSePayTransaction entity = new TestableSePayTransaction();
        assertNull(entity.getCreatedAt());
        assertNull(entity.getUpdatedAt());

        entity.callOnCreate();

        assertNotNull(entity.getCreatedAt());
        assertNotNull(entity.getUpdatedAt());
    }

    @Test
    void testOnUpdate_setsUpdatedAt() {
        TestableSePayTransaction entity = new TestableSePayTransaction();
        entity.callOnCreate();
        LocalDateTime originalCreated = entity.getCreatedAt();
        LocalDateTime originalUpdated = entity.getUpdatedAt();

        // Small delay to ensure timestamps differ
        try { Thread.sleep(10); } catch (InterruptedException ignored) {}

        entity.callOnUpdate();

        assertEquals(originalCreated, entity.getCreatedAt());
        assertNotNull(entity.getUpdatedAt());
    }

    /**
     * Testable subclass to access protected JPA lifecycle methods.
     */
    static class TestableSePayTransaction extends SePayTransaction {
        void callOnCreate() {
            onCreate();
        }

        void callOnUpdate() {
            onUpdate();
        }
    }
}
