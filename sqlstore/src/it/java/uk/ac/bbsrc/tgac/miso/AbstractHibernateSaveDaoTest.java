package uk.ac.bbsrc.tgac.miso;

import static org.junit.Assert.*;

import java.io.IOException;
import java.util.Collection;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Function;

import org.junit.Before;
import org.junit.Test;

import uk.ac.bbsrc.tgac.miso.core.data.Identifiable;
import uk.ac.bbsrc.tgac.miso.core.util.WhineyBiFunction;
import uk.ac.bbsrc.tgac.miso.persistence.impl.HibernateSaveDao;

public abstract class AbstractHibernateSaveDaoTest<T extends Identifiable, D extends HibernateSaveDao<T>>
    extends AbstractDAOTest {

  public static class UpdateParameters<T, R> {

    private final long itemId;
    private final Function<T, R> getter;
    private final BiConsumer<T, R> setter;
    private final R newValue;

    public UpdateParameters(long itemId, Function<T, R> getter, BiConsumer<T, R> setter, R newValue) {
      this.itemId = itemId;
      this.getter = getter;
      this.setter = setter;
      this.newValue = newValue;
    }

    public long getItemId() {
      return itemId;
    }

    public Function<T, R> getGetter() {
      return getter;
    }

    public BiConsumer<T, R> getSetter() {
      return setter;
    }

    public R getNewValue() {
      return newValue;
    }

  }

  private D sut;

  private final Class<? extends T> implClass;
  private final long knownId;
  private final int listSize;

  public AbstractHibernateSaveDaoTest(Class<? extends T> implClass, long knownId, int listSize) {
    this.implClass = implClass;
    this.knownId = knownId;
    this.listSize = listSize;
  }

  public abstract D constructTestSubject();

  protected D getTestSubject() {
    return sut;
  }

  @Before
  public void setup() {
    this.sut = constructTestSubject();
  }

  @Test
  public void testGet() throws Exception {
    T item = sut.get(knownId);
    assertNotNull(item);
    assertEquals(knownId, item.getId());
  }

  @Test
  public void testList() throws Exception {
    List<T> items = sut.list();
    assertNotNull(items);
    assertEquals(listSize, items.size());
  }

  public abstract T getCreateItem();

  @Test
  public void testCreate() throws Exception {
    T item = getCreateItem();
    long savedId = sut.create(item);

    clearSession();

    T saved = getItem(savedId);
    assertNotNull(saved);
  }

  public abstract <R> UpdateParameters<T, R> getUpdateParams();

  @Test
  public <R> void testUpdate() throws Exception {
    UpdateParameters<T, R> updateParams = getUpdateParams();

    T item = getItem(updateParams.getItemId());
    assertNotNull(item);
    assertNotEquals(updateParams.getter.apply(item), updateParams.getNewValue());
    updateParams.setter.accept(item, updateParams.getNewValue());
    sut.update(item);

    clearSession();

    T saved = getItem(updateParams.getItemId());
    assertEquals(updateParams.getter.apply(saved), updateParams.getNewValue());
  }

  protected <R> void testGetBy(WhineyBiFunction<D, R, T> testMethod, R value, Function<T, R> getter) throws Exception {
    T item = testMethod.apply(sut, value);
    assertNotNull(item);
    assertEquals(value, getter.apply(item));
  }

  protected void testGetUsage(WhineyBiFunction<D, T, Long> testMethod, long itemId, Long expectedUsage)
      throws Exception {
    T item = getItem(itemId);
    assertEquals(expectedUsage, testMethod.apply(sut, item));
  }

  private T getItem(long id) throws IOException {
    return (T) currentSession().get(implClass, id);
  }

  protected void testListByIdList(WhineyBiFunction<D, Collection<Long>, List<T>> testMethod, Collection<Long> ids)
      throws Exception {
    List<T> items = testMethod.apply(getTestSubject(), ids);
    assertNotNull(items);
    assertEquals(ids.size(), items.size());
    for (Long id : ids) {
      assertTrue(items.stream().anyMatch(item -> item.getId() == id.longValue()));
    }
  }
}
