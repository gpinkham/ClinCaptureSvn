package com.clinovo.service;

import com.clinovo.model.EDCItemMetadata;
import com.clinovo.util.SignStateRestorer;
import org.akaza.openclinica.bean.login.UserAccountBean;
import org.akaza.openclinica.bean.managestudy.EventDefinitionCRFBean;
import org.akaza.openclinica.bean.managestudy.StudyBean;
import org.akaza.openclinica.bean.managestudy.StudyEventBean;
import org.akaza.openclinica.bean.managestudy.StudyEventDefinitionBean;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * EventDefinitionService.
 */
public interface EventDefinitionService {

	/**
	 * Method that creates new study event definition.
	 *
	 * @param studyBean                StudyBean
	 * @param emailUser                String
	 * @param studyEventDefinitionBean StudyEventDefinitionBean
	 */
	void createStudyEventDefinition(StudyBean studyBean, String emailUser,
									StudyEventDefinitionBean studyEventDefinitionBean);

	/**
	 * Method that updates the whole study event definition with its event definition crfs.
	 *
	 * @param studyBean                        StudyBean
	 * @param updater                          UserAccountBean
	 * @param studyEventDefinitionBean         StudyEventDefinitionBean
	 * @param eventDefinitionCRFsToUpdate      list of parent's EventDefinitionCRFBeans
	 * @param childEventDefinitionCRFsToUpdate list of child's EventDefinitionCRFBeans
	 * @param oldEventDefinitionCRFs           list of EventDefinitionCRFs before update.
	 * @param signStateRestorerMap             Map<Integer, SignStateRestorer>
	 * @param edcItemMetadataMap               HashMap<Integer, ArrayList<EDCItemMetadata>>
	 * @throws Exception an Exception
	 */
	void updateTheWholeStudyEventDefinition(StudyBean studyBean, UserAccountBean updater,
											StudyEventDefinitionBean studyEventDefinitionBean, List<EventDefinitionCRFBean> eventDefinitionCRFsToUpdate,
											List<EventDefinitionCRFBean> childEventDefinitionCRFsToUpdate,
											List<EventDefinitionCRFBean> oldEventDefinitionCRFs, Map<Integer, SignStateRestorer> signStateRestorerMap,
											HashMap<Integer, ArrayList<EDCItemMetadata>> edcItemMetadataMap)
			throws Exception;

	/**
	 * Method that updates all event definition crfs.
	 *
	 * @param studyBean                        StudyBean
	 * @param updater                          UserAccountBean
	 * @param studyEventDefinitionBean         StudyEventDefinitionBean
	 * @param eventDefinitionCRFsToUpdate      list of parent's EventDefinitionCRFBeans
	 * @param childEventDefinitionCRFsToUpdate list of child's EventDefinitionCRFBeans
	 * @param oldEventDefinitionCRFs           list of EventDefinitionCRFs before update.
	 * @param signStateRestorerMap             Map<Integer, SignStateRestorer>
	 * @param edcItemMetadataMap               HashMap<Integer, ArrayList<EDCItemMetadata>>
	 * @throws Exception an Exception
	 */
	void updateAllEventDefinitionCRFs(StudyBean studyBean, UserAccountBean updater,
									  StudyEventDefinitionBean studyEventDefinitionBean, List<EventDefinitionCRFBean> eventDefinitionCRFsToUpdate,
									  List<EventDefinitionCRFBean> childEventDefinitionCRFsToUpdate,
									  List<EventDefinitionCRFBean> oldEventDefinitionCRFs, Map<Integer, SignStateRestorer> signStateRestorerMap,
									  HashMap<Integer, ArrayList<EDCItemMetadata>> edcItemMetadataMap)
			throws Exception;

	/**
	 * Adds new eventDefinitionCRF.
	 *
	 * @param eventDefinitionCRFBean EventDefinitionCRFBean
	 * @param studyBean              StudyBean
	 * @param updater                UserAccountBean
	 */
	void addEventDefinitionCRF(EventDefinitionCRFBean eventDefinitionCRFBean, StudyBean studyBean,
							   UserAccountBean updater);

	/**
	 * Method that updates only the study event definition without its event definition crfs.
	 *
	 * @param studyEventDefinitionBean StudyEventDefinitionBean
	 */
	void updateOnlyTheStudyEventDefinition(StudyEventDefinitionBean studyEventDefinitionBean);

	/**
	 * Fills info for EventDefinitionCRFBeans.
	 *
	 * @param currentStudy             StudyBean
	 * @param studyEventDefinitionBean StudyEventDefinitionBean
	 */
	void fillEventDefinitionCrfs(StudyBean currentStudy, StudyEventDefinitionBean studyEventDefinitionBean);

	/**
	 * Method prepares the SignStateRestorer.
	 *
	 * @param studyEventDefinitionBean StudyEventDefinitionBean
	 * @return Map<Integer, SignStateRestorer>
	 */
	Map<Integer, SignStateRestorer> prepareSignStateRestorer(StudyEventDefinitionBean studyEventDefinitionBean);

	/**
	 * Returns all children EventDefinitionCRFBeans.
	 *
	 * @param studyEventDefinitionBean StudyEventDefinitionBean
	 * @return List<EventDefinitionCRFBean>
	 */
	List<EventDefinitionCRFBean> getAllChildrenEventDefinitionCrfs(StudyEventDefinitionBean studyEventDefinitionBean);

	/**
	 * Returns all parents EventDefinitionCRFBeans.
	 *
	 * @param studyEventDefinitionBean StudyEventDefinitionBean
	 * @return List<EventDefinitionCRFBean>
	 */
	List<EventDefinitionCRFBean> getAllParentsEventDefinitionCrfs(StudyEventDefinitionBean studyEventDefinitionBean);

	/**
	 * Returns all EventDefinitionCRFBeans.
	 *
	 * @param studyEventDefinitionBean StudyEventDefinitionBean
	 * @return List<EventDefinitionCRFBean>
	 */
	List<EventDefinitionCRFBean> getAllEventDefinitionCrfs(StudyEventDefinitionBean studyEventDefinitionBean);

	/**
	 * Returns all StudyEvents.
	 *
	 * @param studyEventDefinitionBean StudyEventDefinitionBean
	 * @return List<StudyEventBean>
	 */
	List<StudyEventBean> getAllStudyEvents(StudyEventDefinitionBean studyEventDefinitionBean);

	/**
	 * Method that removes study event definition.
	 *
	 * @param studyEventDefinitionBean StudyEventDefinitionBean
	 * @param updater                  UserAccountBean
	 * @throws Exception an Exception
	 */
	void removeStudyEventDefinition(StudyEventDefinitionBean studyEventDefinitionBean, UserAccountBean updater)
			throws Exception;

	/**
	 * Method that restores study event definition.
	 *
	 * @param studyEventDefinitionBean StudyEventDefinitionBean
	 * @param updater                  UserAccountBean
	 * @throws Exception an Exception
	 */
	void restoreStudyEventDefinition(StudyEventDefinitionBean studyEventDefinitionBean, UserAccountBean updater)
			throws Exception;

	/**
	 * Removes study event definitions.
	 *
	 * @param studyBean StudyBean
	 * @param updater   UserAccountBean
	 * @throws Exception an Exception
	 */
	void removeStudyEventDefinitions(StudyBean studyBean, UserAccountBean updater) throws Exception;

	/**
	 * Restores study event definitions.
	 *
	 * @param studyBean StudyBean
	 * @param updater   UserAccountBean
	 * @throws Exception an Exception
	 */
	void restoreStudyEventDefinitions(StudyBean studyBean, UserAccountBean updater) throws Exception;
}
