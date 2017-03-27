/*Copyright (c) 2015-2016 wavemaker-com All Rights Reserved.This software is the confidential and proprietary information of wavemaker-com You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the source code license agreement you entered into with wavemaker-com*/
package com.salesdb.service;

/*This is a Studio Managed File. DO NOT EDIT THIS FILE. Your changes may be reverted by Studio.*/


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.wavemaker.runtime.data.dao.WMGenericDao;
import com.wavemaker.runtime.data.exception.EntityNotFoundException;
import com.wavemaker.runtime.data.export.ExportType;
import com.wavemaker.runtime.data.expression.QueryFilter;
import com.wavemaker.runtime.file.model.Downloadable;

import com.salesdb.Channels;
import com.salesdb.Reps;


/**
 * ServiceImpl object for domain model class Channels.
 *
 * @see Channels
 */
@Service("salesdb.ChannelsService")
public class ChannelsServiceImpl implements ChannelsService {

    private static final Logger LOGGER = LoggerFactory.getLogger(ChannelsServiceImpl.class);

    @Autowired
	@Qualifier("salesdb.RepsService")
	private RepsService repsService;

    @Autowired
    @Qualifier("salesdb.ChannelsDao")
    private WMGenericDao<Channels, Integer> wmGenericDao;

    public void setWMGenericDao(WMGenericDao<Channels, Integer> wmGenericDao) {
        this.wmGenericDao = wmGenericDao;
    }

    @Transactional(value = "salesdbTransactionManager")
    @Override
	public Channels create(Channels channels) {
        LOGGER.debug("Creating a new Channels with information: {}", channels);
        Channels channelsCreated = this.wmGenericDao.create(channels);
        if(channelsCreated.getRepses() != null) {
            for(Reps repse : channelsCreated.getRepses()) {
                repse.setChannels(channelsCreated);
                LOGGER.debug("Creating a new child Reps with information: {}", repse);
                repsService.create(repse);
            }
        }
        return channelsCreated;
    }

	@Transactional(readOnly = true, value = "salesdbTransactionManager")
	@Override
	public Channels getById(Integer channelsId) throws EntityNotFoundException {
        LOGGER.debug("Finding Channels by id: {}", channelsId);
        Channels channels = this.wmGenericDao.findById(channelsId);
        if (channels == null){
            LOGGER.debug("No Channels found with id: {}", channelsId);
            throw new EntityNotFoundException(String.valueOf(channelsId));
        }
        return channels;
    }

    @Transactional(readOnly = true, value = "salesdbTransactionManager")
	@Override
	public Channels findById(Integer channelsId) {
        LOGGER.debug("Finding Channels by id: {}", channelsId);
        return this.wmGenericDao.findById(channelsId);
    }


	@Transactional(rollbackFor = EntityNotFoundException.class, value = "salesdbTransactionManager")
	@Override
	public Channels update(Channels channels) throws EntityNotFoundException {
        LOGGER.debug("Updating Channels with information: {}", channels);
        this.wmGenericDao.update(channels);

        Integer channelsId = channels.getId();

        return this.wmGenericDao.findById(channelsId);
    }

    @Transactional(value = "salesdbTransactionManager")
	@Override
	public Channels delete(Integer channelsId) throws EntityNotFoundException {
        LOGGER.debug("Deleting Channels with id: {}", channelsId);
        Channels deleted = this.wmGenericDao.findById(channelsId);
        if (deleted == null) {
            LOGGER.debug("No Channels found with id: {}", channelsId);
            throw new EntityNotFoundException(String.valueOf(channelsId));
        }
        this.wmGenericDao.delete(deleted);
        return deleted;
    }

	@Transactional(readOnly = true, value = "salesdbTransactionManager")
	@Override
	public Page<Channels> findAll(QueryFilter[] queryFilters, Pageable pageable) {
        LOGGER.debug("Finding all Channels");
        return this.wmGenericDao.search(queryFilters, pageable);
    }

    @Transactional(readOnly = true, value = "salesdbTransactionManager")
    @Override
    public Page<Channels> findAll(String query, Pageable pageable) {
        LOGGER.debug("Finding all Channels");
        return this.wmGenericDao.searchByQuery(query, pageable);
    }

    @Transactional(readOnly = true, value = "salesdbTransactionManager")
    @Override
    public Downloadable export(ExportType exportType, String query, Pageable pageable) {
        LOGGER.debug("exporting data in the service salesdb for table Channels to {} format", exportType);
        return this.wmGenericDao.export(exportType, query, pageable);
    }

	@Transactional(readOnly = true, value = "salesdbTransactionManager")
	@Override
	public long count(String query) {
        return this.wmGenericDao.count(query);
    }

    @Transactional(readOnly = true, value = "salesdbTransactionManager")
    @Override
    public Page<Reps> findAssociatedRepses(Integer id, Pageable pageable) {
        LOGGER.debug("Fetching all associated repses");

        StringBuilder queryBuilder = new StringBuilder();
        queryBuilder.append("channels.id = '" + id + "'");

        return repsService.findAll(queryBuilder.toString(), pageable);
    }

    /**
	 * This setter method should only be used by unit tests
	 *
	 * @param service RepsService instance
	 */
	protected void setRepsService(RepsService service) {
        this.repsService = service;
    }

}

