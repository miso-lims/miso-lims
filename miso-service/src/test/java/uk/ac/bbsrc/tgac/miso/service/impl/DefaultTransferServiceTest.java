package uk.ac.bbsrc.tgac.miso.service.impl;

import static org.junit.Assert.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import com.eaglegenomics.simlims.core.Group;
import com.eaglegenomics.simlims.core.User;

import uk.ac.bbsrc.tgac.miso.core.data.Lab;
import uk.ac.bbsrc.tgac.miso.core.data.Sample;
import uk.ac.bbsrc.tgac.miso.core.data.impl.LabImpl;
import uk.ac.bbsrc.tgac.miso.core.data.impl.SampleImpl;
import uk.ac.bbsrc.tgac.miso.core.data.impl.UserImpl;
import uk.ac.bbsrc.tgac.miso.core.data.impl.transfer.Transfer;
import uk.ac.bbsrc.tgac.miso.core.data.impl.transfer.TransferSample;
import uk.ac.bbsrc.tgac.miso.core.data.impl.view.ListTransferView;
import uk.ac.bbsrc.tgac.miso.core.security.AuthorizationManager;
import uk.ac.bbsrc.tgac.miso.core.service.exception.ValidationError;

public class DefaultTransferServiceTest {

  @Mock
  private AuthorizationManager authorizationManager;

  @InjectMocks
  private DefaultTransferService sut;

  @Before
  public void setup() throws IOException {
    MockitoAnnotations.initMocks(this);
    setUser(true);

  }

  private void setUser(boolean isAdmin, Group... groups) throws IOException {
    User user = new UserImpl();
    user.setId(1L);
    Mockito.when(authorizationManager.getCurrentUser()).thenReturn(user);
    Mockito.when(authorizationManager.isAdminUser()).thenReturn(isAdmin);
    Mockito.when(authorizationManager.isGroupMember(Mockito.any())).thenReturn(false);
    if (groups.length > 0) {
      for (Group group : groups) {
        Mockito.when(authorizationManager.isGroupMember(group)).thenReturn(true);
      }
    }
  }

  @Test
  public void testValidateSingleSender() throws Exception {
    Transfer transfer = new Transfer();
    transfer.setSenderLab(makeLab(1L));
    transfer.setSenderGroup(makeGroup(1L));

    List<ValidationError> errors = validate(transfer, null);
    assertError(errors, "Cannot set both sender lab (external) and group (internal)");
  }

  @Test
  public void testValidateSingleRecipient() throws Exception {
    Transfer transfer = new Transfer();
    transfer.setRecipient("Recipient");
    transfer.setRecipientGroup(makeGroup(1L));

    List<ValidationError> errors = validate(transfer, null);
    assertError(errors, "Cannot set both recipient group (internal) and name (external)");
  }

  @Test
  public void testValidateExternalToExternal() throws Exception {
    Transfer transfer = new Transfer();
    transfer.setSenderLab(makeLab(1L));
    transfer.setRecipient("Recipient");

    List<ValidationError> errors = validate(transfer, null);
    assertError(errors, "A transfer cannot be external (lab) to external (named recipient)");
  }

  @Test
  public void validateSenderTypeChangeOne() throws Exception {
    Transfer before = makeTransfer(makeLab(1L), makeGroup(1L));

    Transfer updated = copyTransfer(before);
    updated.setSenderGroup(makeGroup(1L));
    updated.setSenderLab(null);

    List<ValidationError> errors = validate(updated, before);
    assertError(errors, "Cannot change sender between internal (group) and external (lab)");
  }

  @Test
  public void validateSenderTypeChangeTwo() throws Exception {
    Transfer before = makeTransfer(makeGroup(1L), makeGroup(2L));

    Transfer updated = copyTransfer(before);
    updated.setSenderGroup(null);
    updated.setSenderLab(makeLab(1L));

    List<ValidationError> errors = validate(updated, before);
    assertError(errors, "Cannot change sender between internal (group) and external (lab)");
  }

  @Test
  public void validateSenderGroupChangeAllowed() throws Exception {
    Group group1 = makeGroup(1L);
    Group group2 = makeGroup(2L);
    setUser(false, group1, group2);
    Transfer before = makeTransfer(group1, "Recipient");

    Transfer updated = copyTransfer(before);
    updated.setSenderGroup(group2);

    List<ValidationError> errors = validate(updated, before);
    assertNoError(errors, "Only administrators and members of the sender group can change sender group");
    assertNoError(errors, "Sender group must be a group that you are a member of");
  }

  @Test
  public void validateSenderGroupNotAllowed() throws Exception {
    setUser(false);
    Transfer transfer = makeTransfer(makeGroup(1L), makeGroup(2L));

    List<ValidationError> errors = validate(transfer, null);
    assertError(errors, "Sender group must be a group that you are a member of");
  }

  @Test
  public void validateSenderGroupChangeNotAllowed() throws Exception {
    setUser(false);
    Transfer before = makeTransfer(makeGroup(1L), "Recipient");

    Transfer updated = copyTransfer(before);
    updated.setSenderGroup(makeGroup(2L));

    List<ValidationError> errors = validate(updated, before);
    assertError(errors, "Only administrators and members of the sender group can change sender group");
  }

  @Test
  public void testSenderLabChangeAllowed() throws Exception {
    Group group1 = makeGroup(1L);
    setUser(false, group1);
    Transfer before = makeTransfer(makeLab(1L), group1);

    Transfer updated = copyTransfer(before);
    updated.setSenderLab(makeLab(2L));

    List<ValidationError> errors = validate(updated, before);
    assertNoError(errors, "Only administrators and members of the recipient group can change sender lab");
  }

  @Test
  public void testSenderLabChangeNotAllowed() throws Exception {
    setUser(false);
    Transfer before = makeTransfer(makeLab(1L), makeGroup(1L));

    Transfer updated = copyTransfer(before);
    updated.setSenderLab(makeLab(2L));

    List<ValidationError> errors = validate(updated, before);
    assertError(errors, "Only administrators and members of the recipient group can change sender lab");
  }

  @Test
  public void validateRecipientTypeChangeOne() throws Exception {
    Transfer before = makeTransfer(makeGroup(1L), makeGroup(2L));

    Transfer updated = copyTransfer(before);
    updated.setRecipientGroup(null);
    updated.setRecipient("Recipient");

    List<ValidationError> errors = validate(updated, before);
    assertError(errors, "Cannot change recipient between internal (group) and external (named)");
  }

  @Test
  public void validateRecipientTypeChangeTwo() throws Exception {
    Transfer before = makeTransfer(makeGroup(1L), "Recipient");

    Transfer updated = copyTransfer(before);
    updated.setRecipient(null);
    updated.setRecipientGroup(makeGroup(2L));

    List<ValidationError> errors = validate(updated, before);
    assertError(errors, "Cannot change recipient between internal (group) and external (named)");
  }

  @Test
  public void validateRecipientChangeAllowed() throws Exception {
    Group group1 = makeGroup(1L);
    setUser(false, group1);

    Transfer before = makeTransfer(group1, "Recipient");
    Transfer updated = copyTransfer(before);
    updated.setRecipient("Changed");

    List<ValidationError> errors = validate(updated, before);
    assertNoError(errors, "Only administrators and members of the sender group can change recipient");
  }

  @Test
  public void validateRecipientChangeNotAllowed() throws Exception {
    setUser(false);
    Transfer before = makeTransfer(makeGroup(1L), "Recipient");

    Transfer updated = copyTransfer(before);
    updated.setRecipient("Changed");

    List<ValidationError> errors = validate(updated, before);
    assertError(errors, "Only administrators and members of the sender group can change recipient");
  }

  @Test
  public void validateRecipientGroupChangeAllowed() throws Exception {
    Group group1 = makeGroup(1L);
    setUser(false, group1);
    Transfer before = makeTransfer(group1, makeGroup(2L));

    Transfer updated = copyTransfer(before);
    updated.setRecipientGroup(makeGroup(3L));

    List<ValidationError> errors = validate(updated, before);
    assertNoError(errors, "Only administrators and members of the sender group can change recipient group");
  }

  @Test
  public void validateRecipientGroupChangeNotAllowed() throws Exception {
    setUser(false);
    Transfer before = makeTransfer(makeGroup(1L), makeGroup(2L));

    Transfer updated = copyTransfer(before);
    updated.setRecipientGroup(makeGroup(3L));

    List<ValidationError> errors = validate(updated, before);
    assertError(errors, "Only administrators and members of the sender group can change recipient group");
  }

  @Test
  public void testValidateFirstReceipt() throws Exception {
    Transfer transfer = makeTransfer(makeLab(1L), makeGroup(1L));
    addTransferSample(transfer, makeSample(1L));

    List<ValidationError> errors = validate(transfer, null);
    assertTrue(errors.isEmpty());
  }

  @Test
  public void testValidateSingleReceipt() throws Exception {
    Sample sample = makeSample(1L);
    Transfer previousReceipt = makeTransfer(makeLab(1L), makeGroup(1L));
    previousReceipt.setId(1L);
    addTransferSample(previousReceipt, sample);

    Transfer transfer = makeTransfer(makeLab(1L), makeGroup(1L));
    addTransferSample(transfer, sample);

    List<ValidationError> errors = validate(transfer, null);
    assertError(errors, "Items can only have one receipt (external lab to internal group) transfer");
  }

  @Test
  public void testValidateFirstDistribution() throws Exception {
    Transfer transfer = makeTransfer(makeGroup(1L), "Recipient");
    addTransferSample(transfer, makeSample(1L), true);

    List<ValidationError> errors = validate(transfer, null);
    assertTrue(errors.isEmpty());
  }

  @Test
  public void testValidateSingleDistribution() throws Exception {
    Sample sample = makeSample(1L);
    Transfer previousDistribution = makeTransfer(makeGroup(1L), "Recipient 1");
    previousDistribution.setId(1L);
    addTransferSample(previousDistribution, sample, true);

    Transfer transfer = makeTransfer(makeGroup(1L), "Recipient 2");
    addTransferSample(transfer, sample, true);

    List<ValidationError> errors = validate(transfer, null);
    assertError(errors, "Items can only have one distribution (internal group to external named recipient) transfer");
  }

  @Test
  public void testValidateItemChangeAllowed() throws Exception {
    Group group1 = makeGroup(1L);
    setUser(false, group1);

    Transfer before = makeTransfer(group1, "Recipient");
    addTransferSample(before, makeSample(1L), true);
    addTransferSample(before, makeSample(2L), true);

    Transfer updated = copyTransfer(before);
    updated.getSampleTransfers().removeIf(item -> item.getItem().getId() == 2L);

    List<ValidationError> errors = validate(updated, before);
    assertNoError(errors, "Only administrators and members of the sender group can modify items");
  }

  @Test
  public void testValidateItemChangeNotAllowed() throws Exception {
    setUser(false);

    Transfer before = makeTransfer(makeGroup(1L), "Recipient");
    addTransferSample(before, makeSample(1L), true);
    addTransferSample(before, makeSample(2L), true);

    Transfer updated = copyTransfer(before);
    updated.getSampleTransfers().removeIf(item -> item.getItem().getId() == 2L);

    List<ValidationError> errors = validate(updated, before);
    assertError(errors, "Only administrators and members of the sender group can modify items");
  }

  private List<ValidationError> validate(Transfer transfer, Transfer beforeChange) throws IOException {
    List<ValidationError> errors = new ArrayList<>();
    sut.collectValidationErrors(transfer, beforeChange, errors);
    return errors;
  }

  private void assertError(List<ValidationError> errors, String message) {
    assertTrue(errors.stream().anyMatch(error -> message.equals(error.getMessage())));
  }

  private void assertNoError(List<ValidationError> errors, String message) {
    assertFalse(errors.stream().anyMatch(error -> message.equals(error.getMessage())));
  }

  private Transfer makeTransfer(Lab sender, Group recipient) {
    Transfer transfer = new Transfer();
    transfer.setTransferTime(new Date());
    transfer.setSenderLab(sender);
    transfer.setRecipientGroup(recipient);
    return transfer;
  }

  private Transfer makeTransfer(Group sender, String recipient) {
    Transfer transfer = new Transfer();
    transfer.setTransferTime(new Date());
    transfer.setSenderGroup(sender);
    transfer.setRecipient(recipient);
    return transfer;
  }

  private Transfer makeTransfer(Group sender, Group recipient) {
    Transfer transfer = new Transfer();
    transfer.setTransferTime(new Date());
    transfer.setSenderGroup(sender);
    transfer.setRecipientGroup(recipient);
    return transfer;
  }

  private Lab makeLab(long labId) {
    Lab lab = new LabImpl();
    lab.setId(labId);
    return lab;
  }

  private Group makeGroup(long groupId) {
    Group group = new Group();
    group.setId(groupId);
    return group;
  }

  private Sample makeSample(long sampleId) {
    Sample sample = new SampleImpl();
    sample.setId(sampleId);
    return sample;
  }

  private void addTransferSample(Transfer to, Sample sample) {
    addTransferSample(to, sample, null);
  }

  private void addTransferSample(Transfer to, Sample sample, Boolean received) {
    TransferSample item = new TransferSample();
    item.setItem(sample);
    item.setTransfer(to);
    item.setReceived(received);
    if (to.isSaved()) {
      ListTransferView view = new ListTransferView();
      view.setId(to.getId());
      view.setSenderLab(to.getSenderLab());
      view.setSenderGroup(to.getSenderGroup());
      view.setRecipientGroup(to.getRecipientGroup());
      view.setRecipient(to.getRecipient());
      view.setTransferTime(to.getTransferTime());
      sample.getTransferViews().add(view);
    }
    to.getSampleTransfers().add(item);
  }

  private Transfer copyTransfer(Transfer from) {
    Transfer to = new Transfer();
    to.setId(from.getId());
    to.setSenderLab(from.getSenderLab());
    to.setSenderGroup(from.getSenderGroup());
    to.setRecipient(from.getRecipient());
    to.setRecipientGroup(from.getRecipientGroup());
    to.setTransferTime(from.getTransferTime());

    from.getSampleTransfers().forEach(fromItem -> addTransferSample(to, makeSample(fromItem.getItem().getId())));

    return to;
  }

}
