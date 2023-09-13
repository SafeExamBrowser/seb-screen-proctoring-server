package ch.ethz.seb.sps.server.datalayer.dao.impl;

import ch.ethz.seb.sps.server.datalayer.batis.GroupViewMapper;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

class GroupDAOBatisTest {

    @Test
    public void testGetGroupsWithExamDataWithDefaultPaging() {
        //GIVEN
        final GroupViewMapper groupViewMapper = Mockito.mock(GroupViewMapper.class);


        //WHEN
//        when(groupViewMapper.getGroupsWithExamData()).thenReturn();




        //THEN



    }

    @Test
    void createNew() {
    }
}