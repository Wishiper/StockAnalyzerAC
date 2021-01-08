package agprojects.StockAnalyzerAC.enums;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public enum TwsErrorCode {

    CONNECTION_TO_TWS_NOT_ESTABLISHED(502);

    private static final Map<Integer, TwsErrorCode> BY_CODE = new HashMap<>();

    static {
        Arrays.stream(values()).forEach(e -> BY_CODE.put(e.errorCode, e));
    }

    private final int errorCode;

    TwsErrorCode(int errorCode) {
        this.errorCode = errorCode;
    }

    public static Optional<TwsErrorCode> valueOfCode(int code) {
        return Optional.ofNullable(BY_CODE.get(code));
    }
}
