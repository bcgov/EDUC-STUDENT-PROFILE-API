package ca.bc.gov.educ.api.student.profile.schedulers;


import ca.bc.gov.educ.api.student.profile.constants.StudentProfileStatusCodes;
import ca.bc.gov.educ.api.student.profile.repository.DocumentRepository;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import net.javacrumbs.shedlock.core.LockAssert;
import net.javacrumbs.shedlock.spring.annotation.SchedulerLock;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Arrays;

@Component
@Slf4j
public class StudentProfileScheduler {


  private final DocumentRepository documentRepository;

  public StudentProfileScheduler(final DocumentRepository documentRepository) {
    this.documentRepository = documentRepository;
  }

  /**
   * run the job based on configured scheduler(a cron expression) and purge old records from DB.
   */
  @Scheduled(cron = "${scheduled.jobs.remove.blob.contents.document.cron}")
  @SchedulerLock(name = "RemoveBlobContentsFromUploadedDocuments",
    lockAtLeastFor = "PT4H", lockAtMostFor = "PT4H") //midnight job so lock for 4 hours
  @Transactional(propagation = Propagation.REQUIRES_NEW)
  public void removeBlobContentsFromUploadedDocuments() {
    val dateTimeToCompare = LocalDateTime.now().minusHours(24);
    LockAssert.assertLocked();
    val records = this.documentRepository.findAllByRequestStudentRequestStatusCodeInAndFileSizeGreaterThanAndDocumentDataIsNotNull(Arrays.asList(StudentProfileStatusCodes.COMPLETED.toString(), StudentProfileStatusCodes.ABANDONED.toString()), 0);
    if (!records.isEmpty()) {
      for (val document : records) {
        if (document.getRequest().getStatusUpdateDate().isBefore(dateTimeToCompare)) {
          document.setDocumentData(null); // empty the document data.
          document.setFileSize(0);
        }
      }
      this.documentRepository.saveAll(records);
    }
  }
}
