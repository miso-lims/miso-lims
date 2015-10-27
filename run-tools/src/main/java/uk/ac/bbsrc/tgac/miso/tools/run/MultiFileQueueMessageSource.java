/*
 * Copyright (c) 2012. The Genome Analysis Centre, Norwich, UK
 * MISO project contacts: Robert Davey, Mario Caccamo @ TGAC
 * *********************************************************************
 *
 * This file is part of MISO.
 *
 * MISO is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * MISO is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with MISO.  If not, see <http://www.gnu.org/licenses/>.
 *
 * *********************************************************************
 */

package uk.ac.bbsrc.tgac.miso.tools.run;

import java.io.File;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.PriorityBlockingQueue;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.integration.Message;
import org.springframework.integration.MessagingException;
import org.springframework.integration.context.IntegrationObjectSupport;
import org.springframework.integration.core.MessageSource;
import org.springframework.integration.file.DefaultDirectoryScanner;
import org.springframework.integration.file.DirectoryScanner;
import org.springframework.integration.file.FileLocker;
import org.springframework.integration.file.HeadDirectoryScanner;
import org.springframework.integration.file.filters.FileListFilter;
import org.springframework.integration.support.MessageBuilder;
import org.springframework.util.Assert;

/**
 * uk.ac.bbsrc.tgac.miso.notification.core
 * <p/>
 * Modified from the source below to provide a list of files as a result, rather than polling single files sequentially from the queue
 * 
 * @author Rob Davey
 * @date 08-Dec-2010
 * @since 0.0.2
 * 
 *        --------------------------------
 * 
 *        {@link org.springframework.integration.core.MessageSource} that creates messages from a file system directory. To prevent messages
 *        for certain files, you may supply a {@link org.springframework.integration.file.filters.FileListFilter}. By default, an
 *        {@link org.springframework.integration.file.filters.AcceptOnceFileListFilter} is used. It ensures files are picked up only once
 *        from the directory.
 *        <p/>
 *        A common problem with reading files is that a file may be detected before it is ready. The default
 *        {@link org.springframework.integration.file.filters.AcceptOnceFileListFilter} does not prevent this. In most cases, this can be
 *        prevented if the file-writing process renames each file as soon as it is ready for reading. A pattern-matching filter that accepts
 *        only files that are ready (e.g. based on a known suffix), composed with the default
 *        {@link org.springframework.integration.file.filters.AcceptOnceFileListFilter} would allow for this.
 *        <p/>
 *        A {@link java.util.Comparator} can be used to ensure internal ordering of the Files in a
 *        {@link java.util.concurrent.PriorityBlockingQueue}. This does not provide the same guarantees as a
 *        {@link org.springframework.integration.aggregator.ResequencingMessageGroupProcessor}, but in cases where writing files and failure
 *        downstream are rare it might be sufficient.
 *        <p/>
 *        FileReadingMessageSource is fully thread-safe under concurrent <code>receive()</code> invocations and message delivery callbacks.
 * 
 * @author Iwein Fuld
 * @author Mark Fisher
 * @author Oleg Zhurakousky
 */
public class MultiFileQueueMessageSource extends IntegrationObjectSupport implements MessageSource<Set<File>> {

  private static final int DEFAULT_INTERNAL_QUEUE_CAPACITY = 5;

  private static final Log logger = LogFactory.getLog(MultiFileQueueMessageSource.class);

  private volatile Set<File> directories;

  private volatile DirectoryScanner scanner = new DefaultDirectoryScanner();

  private volatile boolean autoCreateDirectory = true;

  /*
   * {@link PriorityBlockingQueue#iterator()} throws {@link java.util.ConcurrentModificationException} in Java 5. There is no locking around
   * the queue, so there is also no iteration.
   */
  private final PriorityBlockingQueue<File> toBeReceived;

  private volatile boolean scanEachPoll = false;

  /**
   * Creates a MultiFileQueueMessageSource with a naturally ordered queue of unbounded capacity.
   */
  public MultiFileQueueMessageSource() {
    this(null);
  }

  /**
   * Creates a MultiFileQueueMessageSource with a bounded queue of the given capacity. This can be used to reduce the memory footprint of
   * this component when reading from a large directory.
   * 
   * @param internalQueueCapacity
   *          the size of the queue used to cache files to be received internally. This queue can be made larger to optimize the directory
   *          scanning. With scanEachPoll set to false and the queue to a large size, it will be filled once and then completely emptied
   *          before a new directory listing is done. This is particularly useful to reduce scans of large numbers of files in a directory.
   */
  public MultiFileQueueMessageSource(int internalQueueCapacity) {
    this(null);
    Assert.isTrue(internalQueueCapacity > 0, "Cannot create a queue with non positive capacity");
    this.setScanner(new HeadDirectoryScanner(internalQueueCapacity));
  }

  /**
   * Creates a MultiFileQueueMessageSource with a {@link java.util.concurrent.PriorityBlockingQueue} ordered with the passed in
   * {@link java.util.Comparator}
   * <p/>
   * The size of the queue used should be large enough to hold all the files in the input directory in order to sort all of them, so
   * restricting the size of the queue is mutually exclusive with ordering. No guarantees about file delivery order can be made under
   * concurrent access.
   * <p/>
   * 
   * @param receptionOrderComparator
   *          the comparator to be used to order the files in the internal queue
   */
  public MultiFileQueueMessageSource(Comparator<File> receptionOrderComparator) {
    this.toBeReceived = new PriorityBlockingQueue<File>(DEFAULT_INTERNAL_QUEUE_CAPACITY, receptionOrderComparator);
  }

  /**
   * Specify the input directories.
   * 
   * @param directories
   *          to monitor
   */
  public void setDirectories(Set<File> directories) {
    Assert.notNull(directories, "directories must not be null");
    this.directories = directories;
  }

  /**
   * Optionally specify a custom scanner, for example the {@link org.springframework.integration.file.RecursiveLeafOnlyDirectoryScanner}
   * 
   * @param scanner
   *          scanner implementation
   */
  public void setScanner(DirectoryScanner scanner) {
    this.scanner = scanner;
  }

  /**
   * Specify whether to create the source directory automatically if it does not yet exist upon initialization. By default, this value is
   * <emphasis>true</emphasis>. If set to <emphasis>false</emphasis> and the source directory does not exist, an Exception will be thrown
   * upon initialization.
   * 
   * @param autoCreateDirectory
   *          should the directory to be monitored be created when this component starts up?
   */
  public void setAutoCreateDirectory(boolean autoCreateDirectory) {
    this.autoCreateDirectory = autoCreateDirectory;
  }

  /**
   * Sets a {@link org.springframework.integration.file.filters.FileListFilter}. By default a
   * {@link org.springframework.integration.file.filters.AbstractFileListFilter} with no bounds is used. In most cases a customized
   * {@link org.springframework.integration.file.filters.FileListFilter} will be needed to deal with modification and duplication concerns.
   * If multiple filters are required a {@link org.springframework.integration.file.filters.CompositeFileListFilter} can be used to group
   * them together.
   * <p/>
   * <b>The supplied filter must be thread safe.</b>.
   * 
   * @param filter
   *          a filter
   */
  public void setFilter(FileListFilter<File> filter) {
    Assert.notNull(filter, "'filter' must not be null");
    this.scanner.setFilter(filter);
  }

  /**
   * Optional. Sets a {@link org.springframework.integration.file.FileLocker} to be used to guard files against duplicate processing.
   * <p/>
   * <b>The supplied FileLocker must be thread safe</b>
   * 
   * @param locker
   *          a locker
   */
  public void setLocker(FileLocker locker) {
    Assert.notNull(locker, "'fileLocker' must not be null.");
    this.scanner.setLocker(locker);
  }

  /**
   * Optional. Set this flag if you want to make sure the internal queue is refreshed with the latest content of the input directories on
   * each poll.
   * <p/>
   * By default this implementation will empty its queue before looking at the directory again. In cases where order is relevant it is
   * important to consider the effects of setting this flag. The internal {@link java.util.concurrent.BlockingQueue} that this class is
   * keeping will more likely be out of sync with the file system if this flag is set to <code>false</code>, but it will change more often
   * (causing expensive reordering) if it is set to <code>true</code>.
   * 
   * @param scanEachPoll
   *          whether or not the component should re-scan (as opposed to not rescanning until the entire backlog has been delivered)
   */
  public void setScanEachPoll(boolean scanEachPoll) {
    this.scanEachPoll = scanEachPoll;
  }

  @Override
  public String getComponentType() {
    return "file:inbound-channel-adapter";
  }

  @Override
  protected void onInit() {
    Assert.notNull(directories, "'directories' must not be null");

    for (File directory : directories) {
      if (!directory.exists() && this.autoCreateDirectory) {
        directory.mkdirs();
      }
      Assert.isTrue(directory.exists(), "Source directory [" + directory + "] does not exist.");
      Assert.isTrue(directory.isDirectory(), "Source path [" + directory + "] does not point to a directory.");
      Assert.isTrue(directory.canRead(), "Source directory [" + directory + "] is not readable.");
    }
  }

  @Override
  public Message<Set<File>> receive() throws MessagingException {
    Message<Set<File>> message = null;

    // rescan only if needed or explicitly configured
    if (scanEachPoll || toBeReceived.isEmpty()) {
      scanInputDirectories();
    }

    // instead of doing a poll() for a single files, drain the whole queue into a set
    Set<File> files = new HashSet<File>();
    toBeReceived.drainTo(files);

    for (File file : files) {
      while ((file != null) && !scanner.tryClaim(file)) {
        files.remove(file);
      }
    }

    if (!files.isEmpty()) {
      message = MessageBuilder.withPayload(files).build();
      if (logger.isDebugEnabled()) {
        logger.debug("Created message: [" + message + "]");
      }
    }
    return message;
  }

  private void scanInputDirectories() {
    for (File directory : directories) {
      List<File> filteredFiles = scanner.listFiles(directory);
      Set<File> freshFiles = new HashSet<File>(filteredFiles);
      if (!freshFiles.isEmpty()) {
        toBeReceived.addAll(freshFiles);
        if (logger.isDebugEnabled()) {
          logger.debug("Added to queue: " + freshFiles);
        }
      }
    }
  }

  /**
   * Adds the failed message back to the 'toBeReceived' queue if there is room.
   * 
   * @param failedMessage
   *          the {@link org.springframework.integration.Message} that failed
   */
  public void onFailure(Message<File> failedMessage) {
    if (logger.isWarnEnabled()) {
      logger.warn("Failed to send: " + failedMessage);
    }
    toBeReceived.offer(failedMessage.getPayload());
  }

  /**
   * The message is just logged. It was already removed from the queue during the call to <code>receive()</code>
   * 
   * @param sentMessage
   *          the message that was successfully delivered
   */
  public void onSend(Message<File> sentMessage) {
    if (logger.isDebugEnabled()) {
      logger.debug("Sent: " + sentMessage);
    }
  }
}
