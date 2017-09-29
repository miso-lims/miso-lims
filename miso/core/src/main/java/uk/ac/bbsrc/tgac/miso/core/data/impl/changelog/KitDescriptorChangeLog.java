package uk.ac.bbsrc.tgac.miso.core.data.impl.changelog;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

import org.hibernate.annotations.Index;
import org.hibernate.annotations.Table;

import uk.ac.bbsrc.tgac.miso.core.data.AbstractChangeLog;
import uk.ac.bbsrc.tgac.miso.core.data.impl.kit.KitDescriptor;

@Entity
@Table(appliesTo = "KitDescriptorChangeLog", indexes = {
    @Index(name = "KitDescriptorChangeLog_kitDescriptorId_changeTime", columnNames = { "kitDescriptorId", "changeTime" }) })
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
