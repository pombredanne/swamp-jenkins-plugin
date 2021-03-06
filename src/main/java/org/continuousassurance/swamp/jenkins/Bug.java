
/* 
  SWAMP Jenkins Plugin

  Copyright 2016 Jared Sweetland, Vamshi Basupalli, James A. Kupsch

  Licensed under the Apache License, Version 2.0 (the "License");
  you may not use this file except in compliance with the License.
  You may obtain a copy of the License at

      http://www.apache.org/licenses/LICENSE-2.0

  Unless required by applicable law or agreed to in writing, software
  distributed under the License is distributed on an "AS IS" BASIS,
  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express implied.
  See the License for the specific language governing permissions and
  limitations under the License.
  */

package org.continuousassurance.swamp.jenkins;

import javax.annotation.CheckForNull;
import java.text.DateFormat;
import java.util.Date;
import java.util.Random;

import org.apache.commons.lang.StringUtils;
import org.jvnet.localizer.LocaleProvider;

import jenkins.model.Jenkins;

import hudson.plugins.analysis.util.model.AbstractAnnotation;
import hudson.plugins.analysis.util.model.Priority;
import org.continuousassurance.swamp.Messages;

/**
 * A serializable Java Bean class representing a warning.
 * <p>
 * Note: this class has a natural ordering that is inconsistent with equals.
 * </p>
 *
 * @author Ulli Hafner
 */
@SuppressWarnings("PMD.CyclomaticComplexity")
public class Bug extends AbstractAnnotation {
    /** Origin of the annotation. */
    //public static final String ORIGIN = "findbugs";
    public static String ORIGIN;

    private static final long serialVersionUID = 5171661552905752370L;
    private static final Random RANDOM = new Random();

    private String tooltip = StringUtils.EMPTY;

    /** Unique hash code of this bug. */
    private HexishString instanceHash;

    /** Computed from firstSeen. */
    private int ageInDays;
    private long firstSeen;
    private int reviewCount;
    private boolean notAProblem;
    private boolean inCloud;
    private boolean shouldBeInCloud;
    private String detailsUrl;

    /** Bug rank that is a replacement for the priority. @since 4.25. */
    private int rank;

    /**
     * Creates a new instance of <code>Bug</code>.
     *
     * @param priority
     *            the priority
     * @param message
     *            the message of the warning
     * @param category
     *            the warning category
     * @param type
     *            the identifier of the warning type
     * @param start
     *            the first line of the line range
     * @param end
     *            the last line of the line range
     */
    public Bug(final Priority priority, final String message, final String category, final String type, final String origin,
            final int start, final int end) {
        //super(priority, message, start, end, category, type);
        //super(priority, message, start, end, origin, type);
    	super(priority, message, start, end, origin + ":" + category, type);
        ORIGIN = origin;
        setOrigin(origin);
    }

    /**
     * Creates a new instance of <code>Bug</code>.
     *
     * @param priority
     *            the priority
     * @param message
     *            the message of the warning
     * @param category
     *            the warning category
     * @param type
     *            the identifier of the warning type
     * @param lineNumber
     *            the line number of the warning in the corresponding file
     */
    public Bug(final Priority priority, final String message, final String category, final String type, final int lineNumber) {
        this(priority, message, category, type, "unavailable", lineNumber, lineNumber);
    }

    /**
     * Creates a new instance of <code>Bug</code> that has no associated line in code (file warning).
     *
     * @param priority
     *            the priority
     * @param message
     *            the message of the warning
     * @param category
     *            the warning category
     * @param type
     *            the identifier of the warning type
     */
    public Bug(final Priority priority, final String message, final String category, final String type) {
        this(priority, message, category, type, "unavailable", 0, 0);
    }

    /**
     * Creates a new instance of <code>Bug</code>.
     *
     * @param priority
     *            the priority
     * @param message
     *            the message of the warning
     * @param category
     *            the warning category
     * @param type
     *            the identifier of the warning type
     * @param start
     *            the first line of the line range
     * @param end
     *            the last line of the line range
     * @param tooltip
     *            the tooltip to show
     */
    public Bug(final Priority priority, final String message, final String category, final String type,
            final int start, final int end, final String tooltip) {
        this(priority, message, category, type, "unavailable", start, end);

        this.tooltip = tooltip;
    }

    /**
     * Sets the rank of this bug.
     *
     * @param bugRank the rank of this bug
     */
    public void setRank(final int bugRank) {
        rank = bugRank;
    }

    /**
     * Returns the rank of this bug.
     *
     * @return the rank
     */
    public int getRank() {
        return rank;
    }

    // CHECKSTYLE:OFF Properties of FindBugs cloud
    @SuppressWarnings("javadoc")
    public long getFirstSeen() {
        return firstSeen;
    }

    void setFirstSeen(final long firstSeen) {
        this.firstSeen = firstSeen;
    }

    @SuppressWarnings("javadoc")
    public void setInCloud(final boolean inCloud) {
        this.inCloud = inCloud;
    }

    @SuppressWarnings("javadoc")
    public boolean isInCloud() {
        return inCloud;
    }

    @SuppressWarnings("javadoc")
    public int getAgeInDays() {
        return ageInDays;
    }

    void setAgeInDays(final int ageInDays) {
        this.ageInDays = ageInDays;
    }

    @SuppressWarnings("javadoc")
    public int getReviewCount() {
        return reviewCount;
    }

    void setReviewCount(final int reviewCount) {
        this.reviewCount = reviewCount;
    }

    @SuppressWarnings("javadoc")
    public boolean isNotAProblem() {
        return notAProblem;
    }

    void setNotAProblem(final boolean notAProblem) {
        this.notAProblem = notAProblem;
    }

    @SuppressWarnings("javadoc")
    public void setShouldBeInCloud(final boolean shouldBeInCloud) {
        this.shouldBeInCloud = shouldBeInCloud;
    }

    @SuppressWarnings("javadoc")
    public boolean isShouldBeInCloud() {
        return shouldBeInCloud;
    }

    @SuppressWarnings("javadoc")
    public void setDetailsUrlTemplate(@CheckForNull final String detailsUrl) {
        this.detailsUrl = detailsUrl;
    }
    // CHECKSTYLE:ON

    /**
     * Rebuilds the priorities mapping.
     *
     * @return the created object
     */
    private Object readResolve() {
        superReadResolve();

        if (instanceHash == null) {
            instanceHash = HexishString.of(String.valueOf(super.hashCode()));
        }

        return this;
    }

    @Override
    public String getToolTip() {
        return StringUtils.defaultIfEmpty(tooltip, FindBugsMessages.getInstance().getMessage(getType(), LocaleProvider.getLocale()));
    }

    @Override
    public String getMessage() {
        return super.getMessage() + getCloudInformation();
    }

    private String getCloudInformation() {
        if (!inCloud && detailsUrl == null) {
            return StringUtils.EMPTY;
        }

        StringBuilder cloudMessage = new StringBuilder();

        appendFirstSeenMessage(cloudMessage);

        int id = RANDOM.nextInt();
        String onclick = "";
        if (detailsUrl != null) {
            onclick = "o=document.getElementById('fb-comments-" + id + "'); "
                    + "o.src='" + String.format(detailsUrl, instanceHash) + "'; "
                    + "o.style.display='block';"
                    + "document.getElementById('fb-arrow-" + id + "').src='/plugin/findbugs/icons/arrow-down.gif';"
                    + "return false";
            cloudMessage.append("<a href='' onclick=\"").append(onclick).append("\">");
        }
        cloudMessage.append(getReviewerMessage());
        if (detailsUrl != null) {
            cloudMessage.append("</a>");

            return String.format("<br/><br/><div onclick=\"%s\">"
                    + "<a href='' onclick=\"%s\"><img src='%s' id='fb-arrow-%s'></a>"
                    + "<img src='%s' title=\"%s\"/>"
                    + "%s<br/>"
                    + "<iframe id='fb-comments-%s' style='display:none;width:400px;height:150px;border=1px solid #BBB'></iframe>"
                    + "</div>",
                    onclick, onclick, getImage("arrow-right.gif"), id, getImage("fb-cloud-icon-small.png"),
                    Messages.FindBugs_Bug_cloudInfo_title(), cloudMessage.toString(), id);
        }
        return cloudMessage.toString();
    }

    private void appendFirstSeenMessage(final StringBuilder cloudMessage) {
        if (ageInDays == 1) {
            cloudMessage.append(Messages.FindBugs_Bug_cloudInfo_seenAt_singular());
        }
        else if (ageInDays > 1) {
            cloudMessage.append(Messages.FindBugs_Bug_cloudInfo_seenAt_plural(ageInDays));
            if (firstSeen > 0) {
                DateFormat format = DateFormat.getDateTimeInstance(DateFormat.SHORT, DateFormat.SHORT);
                cloudMessage.append(' ');
                cloudMessage.append(Messages.FindBugs_Bug_cloudInfo_firstSeen(format.format(new Date(firstSeen))));
            }
        }
        if (cloudMessage.length() > 0) {
            cloudMessage.append(" - ");
        }
    }

    private String getReviewerMessage() {
        String prefix = ", ";
        if (reviewCount == 0) {
            return StringUtils.EMPTY;
        }
        else if (reviewCount == 1) {
            return prefix + Messages.FindBugs_Bug_cloudInfo_reviewer_singular();
        }
        else {
            return prefix + Messages.FindBugs_Bug_cloudInfo_reviewer_plural(reviewCount);
        }
    }

    private String getImage(final String image) {
        Jenkins hudson = Jenkins.getInstance();
        String rootUrl;
        if (hudson == null) {
            rootUrl = StringUtils.EMPTY;
        }
        else {
            rootUrl = hudson.getRootUrl();
        }

        return rootUrl + "/plugin/findbugs/icons/" + image;
    }

    /**
     * Sets the unique hash code of this bug.
     *
     * @param instanceHash the instance hash as generated by the FindBugs library
     */
    public void setInstanceHash(final String instanceHash) {
        this.instanceHash = HexishString.of(instanceHash);
    }

    @Override
    public int hashCode() {
        return 31 + ((instanceHash == null) ? 0 : instanceHash.hashCode()); // NOCHECKSTYLE
    }

    @Override
    public boolean equals(final Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        Bug other = (Bug)obj;
        if (instanceHash == null) {
            if (other.instanceHash != null) {
                return false;
            }
        }
        else if (!instanceHash.equals(other.instanceHash)) {
            return false;
        }
        return true;
    }
}

