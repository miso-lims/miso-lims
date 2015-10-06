package uk.ac.bbsrc.tgac.miso.webapp.controller.test;

import org.junit.Ignore;
import org.junit.Test;

public class DownloadControllerTestSuite {

   //TODO: Controller tests as per below

   @Ignore
   @Test
   public void doNothingTest() {
      // Placeholder test to stop empty file failing unit tests.
   }

/*   @InjectMocks
   private DownloadController downloadController;

   private MockMvc downloadMvc;
   @Mock
   private SecurityManager securityManager;
   @Mock
   private RequestManager requestManager;
   @Mock
   private User user;
   @Mock
   private Project project;
   @Mock
   private Authentication authentication;
   @Mock
   private FilesManager filesManager;

   @Before
   public void setup() throws Exception {
      MockitoAnnotations.initMocks(this);
      this.downloadMvc = MockMvcBuilders.standaloneSetup(downloadController).build();
   }

   @SuppressWarnings("unchecked")
   @Test
   public void deleteTest() throws Exception {
      final String fileName = "1";
      when(project.userCanWrite(any(User.class))).thenReturn(true);
      when(requestManager.getProjectById(anyLong())).thenReturn(project);
      when(securityManager.getUserByLoginName(anyString())).thenReturn(user);
      when(authentication.getName()).thenReturn("Dr Admin");
      final SecurityContextImpl context = new SecurityContextImpl();
      context.setAuthentication(authentication);
      SecurityContextHolder.setContext(context);
      when(filesManager.getFileNames(any(Class.class), any(String.class))).thenReturn(new ArrayList<String>(Arrays.asList(fileName)));

      downloadMvc.perform(get("/download/project/delete/{id}/{hashcode}", 1, fileName.hashCode())).andExpect(status().isOk());
      verify(filesManager).deleteFile(any(Class.class), any(String.class), eq(String.valueOf(fileName)));
   }

   @After
   public void tearDown() throws Exception {
      Mockito.validateMockitoUsage();
   }*/

}