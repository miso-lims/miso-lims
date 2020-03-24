package uk.ac.bbsrc.tgac.miso.dto;

import java.util.Set;
import java.util.function.Function;

import uk.ac.bbsrc.tgac.miso.core.data.Boxable;
import uk.ac.bbsrc.tgac.miso.core.data.impl.transfer.Transfer;
import uk.ac.bbsrc.tgac.miso.core.data.impl.transfer.TransferItem;

public interface ReceivableDto<T extends Boxable, R extends TransferItem<T>> {

  public String getReceivedTime();

  public void setReceivedTime(String receivedTime);

  public Long getSenderLabId();

  public void setSenderLabId(Long senderLabId);

  public Long getRecipientGroupId();

  public void setRecipientGroupId(Long recipientGroupId);

  public Boolean isReceived();

  public void setReceived(Boolean received);

  public Boolean isReceiptQcPassed();

  public void setReceiptQcPassed(Boolean receiptQcPassed);

  public String getReceiptQcNote();

  public void setReceiptQcNote(String receiptQcNote);

  public R makeTransferItem();

  public Function<Transfer, Set<R>> getTransferItemsFunction();

}
