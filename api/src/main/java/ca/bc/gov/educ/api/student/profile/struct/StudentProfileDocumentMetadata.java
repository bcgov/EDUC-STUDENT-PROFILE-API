package ca.bc.gov.educ.api.student.profile.struct;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class StudentProfileDocumentMetadata implements Serializable {
  private static final long serialVersionUID = 976316524732562350L;
  String studentRequestID;
  String digitalID;
  String documentID;
  String documentTypeCode;
  String fileName;
  String fileExtension;
  Integer fileSize;
  String createDate;
}
