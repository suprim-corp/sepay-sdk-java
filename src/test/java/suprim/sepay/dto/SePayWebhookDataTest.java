package suprim.sepay.dto;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import suprim.sepay.enums.TransferType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for SePayWebhookData DTO.
 */
class SePayWebhookDataTest {

    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
    }

    @Test
    void testBuilder() {
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
            .description("Test payment")
            .transferAmount(1700000L)
            .referenceCode("FT123456789")
            .accumulated(5000000L)
            .build();

        assertEquals(123456L, data.getId());
        assertEquals("MBBank", data.getGateway());
        assertEquals(now, data.getTransactionDate());
        assertEquals("0359123456", data.getAccountNumber());
        assertEquals("001", data.getSubAccount());
        assertEquals("ABC123", data.getCode());
        assertEquals("Thanh toan SE123456", data.getContent());
        assertEquals(TransferType.IN, data.getTransferType());
        assertEquals("Test payment", data.getDescription());
        assertEquals(1700000L, data.getTransferAmount());
        assertEquals("FT123456789", data.getReferenceCode());
        assertEquals(5000000L, data.getAccumulated());
    }

    @Test
    void testBuilder_nullableFields() {
        SePayWebhookData data = SePayWebhookData.builder()
            .id(123456L)
            .gateway("MBBank")
            .transactionDate(LocalDateTime.now())
            .accountNumber("0359123456")
            .content("Test")
            .transferType(TransferType.IN)
            .transferAmount(100000L)
            .build();

        assertNull(data.getSubAccount());
        assertNull(data.getCode());
        assertNull(data.getDescription());
        assertNull(data.getReferenceCode());
        assertNull(data.getAccumulated());
    }

    @Test
    void testJsonDeserialization() throws Exception {
        String json = "{" +
            "\"id\": 999999," +
            "\"gateway\": \"MBBank\"," +
            "\"transactionDate\": \"2024-05-25 21:11:02\"," +
            "\"accountNumber\": \"0359123456\"," +
            "\"subAccount\": null," +
            "\"code\": null," +
            "\"content\": \"Thanh toan QR SE123456\"," +
            "\"transferType\": \"in\"," +
            "\"description\": \"Full SMS content\"," +
            "\"transferAmount\": 1700000," +
            "\"referenceCode\": \"FT123456789\"," +
            "\"accumulated\": 0" +
            "}";

        SePayWebhookData data = objectMapper.readValue(json, SePayWebhookData.class);

        assertEquals(999999L, data.getId());
        assertEquals("MBBank", data.getGateway());
        assertNotNull(data.getTransactionDate());
        assertEquals(2024, data.getTransactionDate().getYear());
        assertEquals(5, data.getTransactionDate().getMonthValue());
        assertEquals(25, data.getTransactionDate().getDayOfMonth());
        assertEquals(21, data.getTransactionDate().getHour());
        assertEquals(11, data.getTransactionDate().getMinute());
        assertEquals(2, data.getTransactionDate().getSecond());
        assertEquals("0359123456", data.getAccountNumber());
        assertEquals("Thanh toan QR SE123456", data.getContent());
        assertEquals(TransferType.IN, data.getTransferType());
        assertEquals("Full SMS content", data.getDescription());
        assertEquals(1700000L, data.getTransferAmount());
        assertEquals("FT123456789", data.getReferenceCode());
        assertEquals(0L, data.getAccumulated());
    }

    @Test
    void testJsonDeserialization_transferTypeOut() throws Exception {
        String json = "{" +
            "\"id\": 123456," +
            "\"gateway\": \"VietinBank\"," +
            "\"transactionDate\": \"2024-12-09 10:30:00\"," +
            "\"accountNumber\": \"0123456789\"," +
            "\"content\": \"Chuyen tien\"," +
            "\"transferType\": \"out\"," +
            "\"transferAmount\": 500000" +
            "}";

        SePayWebhookData data = objectMapper.readValue(json, SePayWebhookData.class);

        assertEquals(TransferType.OUT, data.getTransferType());
    }

    @Test
    void testJsonDeserialization_caseInsensitiveTransferType() throws Exception {
        String json = "{" +
            "\"id\": 123456," +
            "\"gateway\": \"MBBank\"," +
            "\"transactionDate\": \"2024-12-09 10:30:00\"," +
            "\"accountNumber\": \"0123456789\"," +
            "\"content\": \"Test\"," +
            "\"transferType\": \"IN\"," +
            "\"transferAmount\": 500000" +
            "}";

        SePayWebhookData data = objectMapper.readValue(json, SePayWebhookData.class);

        assertEquals(TransferType.IN, data.getTransferType());
    }

    @Test
    void testDateFormat_multipleFormats() throws Exception {
        // Test with different date formats to ensure format is correct
        String json = "{" +
            "\"id\": 123456," +
            "\"gateway\": \"MBBank\"," +
            "\"transactionDate\": \"2024-01-01 00:00:00\"," +
            "\"accountNumber\": \"0123456789\"," +
            "\"content\": \"Test\"," +
            "\"transferType\": \"in\"," +
            "\"transferAmount\": 500000" +
            "}";

        SePayWebhookData data = objectMapper.readValue(json, SePayWebhookData.class);

        assertEquals(2024, data.getTransactionDate().getYear());
        assertEquals(1, data.getTransactionDate().getMonthValue());
        assertEquals(1, data.getTransactionDate().getDayOfMonth());
        assertEquals(0, data.getTransactionDate().getHour());
        assertEquals(0, data.getTransactionDate().getMinute());
        assertEquals(0, data.getTransactionDate().getSecond());
    }

    // Edge case tests

    @Test
    void testJsonDeserialization_largeTransactionAmount() throws Exception {
        String json = "{" +
            "\"id\": 123456," +
            "\"gateway\": \"MBBank\"," +
            "\"transactionDate\": \"2024-12-09 10:30:00\"," +
            "\"accountNumber\": \"0123456789\"," +
            "\"content\": \"Large payment\"," +
            "\"transferType\": \"in\"," +
            "\"transferAmount\": 999999999999" +
            "}";

        SePayWebhookData data = objectMapper.readValue(json, SePayWebhookData.class);

        assertEquals(999999999999L, data.getTransferAmount());
    }

    @Test
    void testJsonDeserialization_largeTransactionId() throws Exception {
        String json = "{" +
            "\"id\": 9223372036854775807," +
            "\"gateway\": \"MBBank\"," +
            "\"transactionDate\": \"2024-12-09 10:30:00\"," +
            "\"accountNumber\": \"0123456789\"," +
            "\"content\": \"Max ID test\"," +
            "\"transferType\": \"in\"," +
            "\"transferAmount\": 100000" +
            "}";

        SePayWebhookData data = objectMapper.readValue(json, SePayWebhookData.class);

        assertEquals(Long.MAX_VALUE, data.getId());
    }

    @Test
    void testJsonDeserialization_longContent() throws Exception {
        String longContent = "A".repeat(500);
        String json = "{" +
            "\"id\": 123456," +
            "\"gateway\": \"MBBank\"," +
            "\"transactionDate\": \"2024-12-09 10:30:00\"," +
            "\"accountNumber\": \"0123456789\"," +
            "\"content\": \"" + longContent + "\"," +
            "\"transferType\": \"in\"," +
            "\"transferAmount\": 100000" +
            "}";

        SePayWebhookData data = objectMapper.readValue(json, SePayWebhookData.class);

        assertEquals(longContent, data.getContent());
        assertEquals(500, data.getContent().length());
    }

    @Test
    void testJsonDeserialization_emptyOptionalStrings() throws Exception {
        String json = "{" +
            "\"id\": 123456," +
            "\"gateway\": \"MBBank\"," +
            "\"transactionDate\": \"2024-12-09 10:30:00\"," +
            "\"accountNumber\": \"0123456789\"," +
            "\"subAccount\": \"\"," +
            "\"code\": \"\"," +
            "\"content\": \"Test\"," +
            "\"transferType\": \"in\"," +
            "\"description\": \"\"," +
            "\"transferAmount\": 100000," +
            "\"referenceCode\": \"\"" +
            "}";

        SePayWebhookData data = objectMapper.readValue(json, SePayWebhookData.class);

        assertEquals("", data.getSubAccount());
        assertEquals("", data.getCode());
        assertEquals("", data.getDescription());
        assertEquals("", data.getReferenceCode());
    }

    @Test
    void testJsonDeserialization_vietnameseContent() throws Exception {
        String json = "{" +
            "\"id\": 123456," +
            "\"gateway\": \"MBBank\"," +
            "\"transactionDate\": \"2024-12-09 10:30:00\"," +
            "\"accountNumber\": \"0123456789\"," +
            "\"content\": \"Chuyen tien thanh toan don hang SE123456\"," +
            "\"transferType\": \"in\"," +
            "\"transferAmount\": 100000" +
            "}";

        SePayWebhookData data = objectMapper.readValue(json, SePayWebhookData.class);

        assertEquals("Chuyen tien thanh toan don hang SE123456", data.getContent());
    }

    @Test
    void testJsonDeserialization_zeroAccumulated() throws Exception {
        String json = "{" +
            "\"id\": 123456," +
            "\"gateway\": \"MBBank\"," +
            "\"transactionDate\": \"2024-12-09 10:30:00\"," +
            "\"accountNumber\": \"0123456789\"," +
            "\"content\": \"Test\"," +
            "\"transferType\": \"in\"," +
            "\"transferAmount\": 100000," +
            "\"accumulated\": 0" +
            "}";

        SePayWebhookData data = objectMapper.readValue(json, SePayWebhookData.class);

        assertEquals(0L, data.getAccumulated());
    }

    @Test
    void testJsonDeserialization_specialCharsInContent() throws Exception {
        String json = "{" +
            "\"id\": 123456," +
            "\"gateway\": \"MBBank\"," +
            "\"transactionDate\": \"2024-12-09 10:30:00\"," +
            "\"accountNumber\": \"0123456789\"," +
            "\"content\": \"Payment - SE123 / Order #456\"," +
            "\"transferType\": \"in\"," +
            "\"transferAmount\": 100000" +
            "}";

        SePayWebhookData data = objectMapper.readValue(json, SePayWebhookData.class);

        assertEquals("Payment - SE123 / Order #456", data.getContent());
    }

    @Test
    void testSetters() {
        SePayWebhookData data = new SePayWebhookData();
        LocalDateTime now = LocalDateTime.now();

        data.setId(999L);
        data.setGateway("VCB");
        data.setTransactionDate(now);
        data.setAccountNumber("1234567890");
        data.setSubAccount("SUB");
        data.setCode("CODE");
        data.setContent("Test content");
        data.setTransferType(TransferType.OUT);
        data.setDescription("Description");
        data.setTransferAmount(500000L);
        data.setReferenceCode("REF");
        data.setAccumulated(1000000L);

        assertEquals(999L, data.getId());
        assertEquals("VCB", data.getGateway());
        assertEquals(now, data.getTransactionDate());
        assertEquals("1234567890", data.getAccountNumber());
        assertEquals("SUB", data.getSubAccount());
        assertEquals("CODE", data.getCode());
        assertEquals("Test content", data.getContent());
        assertEquals(TransferType.OUT, data.getTransferType());
        assertEquals("Description", data.getDescription());
        assertEquals(500000L, data.getTransferAmount());
        assertEquals("REF", data.getReferenceCode());
        assertEquals(1000000L, data.getAccumulated());
    }

    @Test
    void testDefaultConstructor() {
        SePayWebhookData data = new SePayWebhookData();

        assertNull(data.getId());
        assertNull(data.getGateway());
        assertNull(data.getTransactionDate());
        assertNull(data.getAccountNumber());
        assertNull(data.getSubAccount());
        assertNull(data.getCode());
        assertNull(data.getContent());
        assertNull(data.getTransferType());
        assertNull(data.getDescription());
        assertNull(data.getTransferAmount());
        assertNull(data.getReferenceCode());
        assertNull(data.getAccumulated());
    }

    @Test
    void testJsonDeserialization_allVietnameseBanks() throws Exception {
        String[] banks = {"MBBank", "VCB", "Vietcombank", "BIDV", "VietinBank", "ACB", "Techcombank", "TPBank", "VPBank", "Sacombank"};

        for (String bank : banks) {
            String json = "{" +
                "\"id\": 123456," +
                "\"gateway\": \"" + bank + "\"," +
                "\"transactionDate\": \"2024-12-09 10:30:00\"," +
                "\"accountNumber\": \"0123456789\"," +
                "\"content\": \"Test\"," +
                "\"transferType\": \"in\"," +
                "\"transferAmount\": 100000" +
                "}";

            SePayWebhookData data = objectMapper.readValue(json, SePayWebhookData.class);

            assertEquals(bank, data.getGateway());
        }
    }
}
