package uk.ac.bbsrc.tgac.miso.persistence.impl;

import static org.junit.Assert.*;

import java.io.IOException;
import java.util.List;

import org.mockito.InjectMocks;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

import uk.ac.bbsrc.tgac.miso.AbstractDAOTest;
import uk.ac.bbsrc.tgac.miso.persistence.impl.QueryBuilder;
import uk.ac.bbsrc.tgac.miso.core.data.impl.view.ListPoolView;
import uk.ac.bbsrc.tgac.miso.persistence.impl.HibernateListPoolViewDao;

public class HibernateListPoolViewDaoIT extends AbstractDAOTest {

    @PersistenceContext
    private EntityManager entityManager;

    @InjectMocks
    private HibernateListPoolViewDao sut;

    @Before
    public void setup() throws IOException {
        sut = new HibernateListPoolViewDao();
        sut.setEntityManager(entityManager);
    }

    public void assertWorksetHasPools(long worksetId, int expectedCount) {
        QueryBuilder<ListPoolView, ListPoolView> builder = new QueryBuilder<>(currentSession(), ListPoolView.class, ListPoolView.class);
        sut.restrictPaginationByWorksetId(builder, worksetId, msg -> fail(msg));

        List<ListPoolView> results = builder.getResultList();
        assertEquals(worksetId, expectedCount, results.size());
    }

    @Test
    public void testRestrictPaginationByWorksetId(){
        assertWorksetHasPools(1L, 2);
        assertWorksetHasPools(2L, 2);
    }

}