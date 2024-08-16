package uk.ac.bbsrc.tgac.miso.core.data.impl.changelog;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import uk.ac.bbsrc.tgac.miso.core.data.AbstractChangeLog;
import uk.ac.bbsrc.tgac.miso.core.data.impl.kit.KitDescriptor;

@Entity
@Table(name = "KitDescriptorChangeLog", indexes = {
    @Index(name = "KitDescriptorChangeLog_kitDescriptorId_changeTime",
        columnList = "kitDescriptorId, changeTime")})
public class KitDescriptorChangeLog extends AbstractChangeLog {

  private static final long serialVersionUID = 1L;

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long kitDescriptorChangeLogId;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "kitDescriptorId", nullable = false, updatable = false)
  private KitDescriptor kitDescriptor;

  @Override
  public Long getId() {
    return kitDescriptor.getId();
  }

  @Override
  public void setId(Long id) {
    kitDescriptor.setId(id);
  }

  public Long getKitDescriptorChangeLogId() {
    return kitDescriptorChangeLogId;
  }

  public KitDescriptor getKitDescriptor() {
    return kitDescriptor;
  }

  public void setKitDescriptor(KitDescriptor kitDescriptor) {
    this.kitDescriptor = kitDescriptor;
  }

}
