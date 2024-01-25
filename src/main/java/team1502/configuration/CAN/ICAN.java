package team1502.configuration.CAN;
import java.util.List;


public interface ICAN {
    String getName();
    CanInfo getCanInfo();
    default boolean hasCanInfo() {return getCanInfo() != null;}
    default Integer getCanId() {return getCanInfo().deviceNumber;}
    default void setCanId(int number) {getCanInfo().deviceNumber = number;}

    void addError(String errorMessage);
    List<String> getErrors();
    default boolean hasErrors() {return getErrors().isEmpty();}
}