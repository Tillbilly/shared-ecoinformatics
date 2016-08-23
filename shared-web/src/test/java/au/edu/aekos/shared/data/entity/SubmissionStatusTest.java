package au.edu.aekos.shared.data.entity;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;
import java.util.List;

import org.apache.commons.collections4.ListUtils;
import org.junit.Test;

public class SubmissionStatusTest {

	private final List<SubmissionStatus> deletableStatuses = Arrays.asList(
			
			SubmissionStatus.DISCARDED,
			SubmissionStatus.REJECTED,
			SubmissionStatus.SUBMITTED,
			SubmissionStatus.RESUBMITTED,
			SubmissionStatus.SAVED,
			SubmissionStatus.REJECTED_SAVED);
	
	private final List<SubmissionStatus> editableStatuses = Arrays.asList(
			SubmissionStatus.SAVED,
			SubmissionStatus.SUBMITTED,
			SubmissionStatus.RESUBMITTED);

	private final List<SubmissionStatus> modifiableStatuses = Arrays.asList(
			SubmissionStatus.REJECTED,
			SubmissionStatus.REJECTED_SAVED);

	private final List<SubmissionStatus> notLinkableStatuses = Arrays.asList(
			SubmissionStatus.SAVED,
			SubmissionStatus.REJECTED_SAVED,
			SubmissionStatus.DELETED);
	
	private final List<SubmissionStatus> publicationCertificateAvailableStatuses = Arrays.asList(SubmissionStatus.PUBLISHED);
	
	/**
	 * Can we tell when a submission can be deleted?
	 */
	@Test
	public void testIsDeletable01() {
		for (SubmissionStatus currStatus : deletableStatuses) {
			assertTrue(currStatus + " failed!", currStatus.isDeletable());
		}
	}

	/**
	 * Make sure all other status CANNOT be deleted!
	 */
	@Test
	public void testIsDeletable02() {
		List<SubmissionStatus> notDeletableStatuses = ListUtils.subtract(Arrays.asList(SubmissionStatus.values()), deletableStatuses);
		for (SubmissionStatus currStatus : notDeletableStatuses) {
			assertFalse(currStatus + " failed!", currStatus.isDeletable());
		}
	}
	
	/**
	 * Can we tell when a submission can be edited?
	 */
	@Test
	public void testIsEditable01() {
		for (SubmissionStatus currStatus : editableStatuses) {
			assertTrue(currStatus + " failed!", currStatus.isEditable());
		}
	}

	/**
	 * Make sure all other statuses CANNOT be edited!
	 */
	@Test
	public void testIsEditable02() {
		List<SubmissionStatus> notEditableStatuses = ListUtils.subtract(Arrays.asList(SubmissionStatus.values()), editableStatuses);
		for (SubmissionStatus currStatus : notEditableStatuses) {
			assertFalse(currStatus + " failed!", currStatus.isEditable());
		}
	}
	
	/**
	 * Can we tell when a submission can be modified?
	 */
	@Test
	public void testIsModfiable01() {
		for (SubmissionStatus currStatus : modifiableStatuses) {
			assertTrue(currStatus + " failed!", currStatus.isModifiable());
		}
	}

	/**
	 * Make sure all other statuses CANNOT be modified!
	 */
	@Test
	public void testIsModifiable02() {
		List<SubmissionStatus> notModifiableStatuses = ListUtils.subtract(Arrays.asList(SubmissionStatus.values()), modifiableStatuses);
		for (SubmissionStatus currStatus : notModifiableStatuses) {
			assertFalse(currStatus + " failed!", currStatus.isModifiable());
		}
	}
	
	/**
	 * Can we tell when a submission has a publication certificate available?
	 */
	@Test
	public void testIsPublicationCertificateAvailable01() {
		for (SubmissionStatus currStatus : publicationCertificateAvailableStatuses) {
			assertTrue(currStatus + " failed!", currStatus.isPublicationCertificateAvailable());
		}
	}
	
	/**
	 * Make sure all other statuses DO NOT have a publication certificate available!
	 */
	@Test
	public void testIsPublicationCertificateAvailable02() {
		List<SubmissionStatus> noPublicationCertificateAvailableStatuses = ListUtils.subtract(Arrays.asList(SubmissionStatus.values()), publicationCertificateAvailableStatuses);
		for (SubmissionStatus currStatus : noPublicationCertificateAvailableStatuses) {
			assertFalse(currStatus + " failed!", currStatus.isPublicationCertificateAvailable());
		}
	}
	
	/**
	 * Can we tell when something IS deleted?
	 */
	@Test
	public void testIsDeleted01() {
		assertTrue(SubmissionStatus.DELETED.isDeleted());
	}
	
	/**
	 * Can we tell when something IS NOT deleted?
	 */
	@Test
	public void testIsDeleted02() {
		List<SubmissionStatus> notDeletedStatuses = ListUtils.subtract(Arrays.asList(SubmissionStatus.values()), Arrays.asList(SubmissionStatus.DELETED));
		for (SubmissionStatus currStatus : notDeletedStatuses) {
			assertFalse(currStatus + " failed!", currStatus.isDeleted());
		}
	}
	
	/**
	 * Can we tell when something IS linkable?
	 */
	@Test
	public void testIsLinkable01() {
		List<SubmissionStatus> linkableStatuses = ListUtils.subtract(Arrays.asList(SubmissionStatus.values()), notLinkableStatuses);
		for (SubmissionStatus currStatus : linkableStatuses) {
			assertTrue(currStatus + " failed!", currStatus.isLinkable());
		}
	}
	
	/**
	 * Can we tell when something IS NOT linkable?
	 */
	@Test
	public void testIsLinkable02() {
		for (SubmissionStatus currStatus : notLinkableStatuses) {
			assertFalse(currStatus + " failed!", currStatus.isLinkable());
		}
	}
}
