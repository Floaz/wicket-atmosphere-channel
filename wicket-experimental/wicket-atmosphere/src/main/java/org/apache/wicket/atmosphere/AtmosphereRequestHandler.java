/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.wicket.atmosphere;

import java.util.Collection;
import org.apache.wicket.Application;
import org.apache.wicket.Page;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.protocol.http.WebApplication;
import org.apache.wicket.request.IRequestCycle;
import org.apache.wicket.request.IRequestHandler;
import org.apache.wicket.request.cycle.RequestCycle;


/**
 * Handles pseudo requests triggered by an event. An {@link AjaxRequestTarget} is scheduled and the
 * subscribed methods are invoked.
 * 
 * @author papegaaij
 * @author floaz
 */
public class AtmosphereRequestHandler implements IRequestHandler
{
	private PageKey pageKey;

	private Object event;

	private Collection<EventSubscription> subscriptions;

	private boolean ajaxRequestScheduled = false;

	/**
	 * Construct.
	 * 
	 * @param pageKey
	 * @param subscriptions
	 * @param event
	 */
	public AtmosphereRequestHandler(PageKey pageKey, Collection<EventSubscription> subscriptions,
		Object event)
	{
		this.pageKey = pageKey;
		this.subscriptions = subscriptions;
		this.event = event;
	}

	@Override
	public void respond(IRequestCycle requestCycle)
	{
		Page page = (Page)Application.get().getMapperContext().getPageInstance(pageKey.getPageId());
		AjaxRequestTarget target = WebApplication.get().newAjaxRequestTarget(page);
		executeHandlers(target, page);
	}

	private void executeHandlers(AjaxRequestTarget target, Page page)
	{
		for (EventSubscription curSubscription : subscriptions)
		{					
			if(!ajaxRequestScheduled)
			{
				ajaxRequestScheduled = true;
				RequestCycle.get().scheduleRequestHandlerAfterCurrent(target);
			}
			
			curSubscription.call(target, event);
		}
	}

	@Override
	public void detach(IRequestCycle requestCycle)
	{
	}
}
