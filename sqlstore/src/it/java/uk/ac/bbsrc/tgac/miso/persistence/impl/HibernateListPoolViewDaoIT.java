package uk.ac.bbsrc.tgac.miso.persistence.impl;

import static org.junit.Assert.*;
import static org.hamcrest.Matchers.contains;

import java.io.IOException;
import java.util.List;
import java.util.ArrayList;

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

    public void assertWorksetHasPools(long worksetId,long... expectedPoolIds) {
        QueryBuilder<ListPoolView, ListPoolView> builder = new QueryBuilder<>(currentSession(), ListPoolView.class, ListPoolView.class);
        sut.restrictPaginationByWorksetId(builder, worksetId, msg -> fail(msg));

        List<ListPoolView> results = builder.getResultList();
        List<Long> actualPoolIds = new ArrayList<>();
        for(ListPoolView row: results){
            actualPoolIds.add(row.getId());
        }

        for(long expectedId: expectedPoolIds){
            assertTrue("Expected poolID "+ expectedId + " for worksetID " + worksetId + " but it was not in " + actualPoolIds, actualPoolIds.contains(expectedId));
        }

    }

    @Test
    public void testRestrictPaginationByWorksetId(){
        assertWorksetHasPools(1L, 1L, 2L);
        assertWorksetHasPools(2L, 1L);
    }

}