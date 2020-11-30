/*
 * 
 * Copyright 2014 Jules White
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *     http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * 
 */

package org.magnum.mobilecloud.video;

import java.io.IOException;
import java.security.Principal;
import java.util.Collection;
import java.util.List;

import javax.servlet.http.HttpServletResponse;

import org.magnum.mobilecloud.video.repository.Video;
import org.magnum.mobilecloud.video.repository.VideoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class VideoController {
	
	@Autowired
	private VideoRepository videos;
	
	
	/**
	 * Get the video list from server
	 * @return
	 * @throws IOException
	 */
	@RequestMapping(value="/video", method=RequestMethod.GET)
	 public @ResponseBody Collection<Video> getVideoList() throws IOException {
		 return (Collection<Video>) videos.findAll();
	 }
	
	@RequestMapping(value="/video", method=RequestMethod.POST)
	 public @ResponseBody Video addVideo(@RequestBody Video v) throws IOException {
		return videos.save(v);
	 }
	
	@RequestMapping(value="/video/{id}/like", method=RequestMethod.POST)
	public @ResponseBody void likeVideo(@PathVariable("id")  long id , Principal p, HttpServletResponse response) {
		if (videos.findOne(id) == null) {
			 response.setStatus(HttpStatus.NOT_FOUND.value()); 
			 return;
		}
		
		Video findOne = videos.findOne(id);
		if (findOne.getLikedBy().contains(p.getName())) {
			 response.setStatus(HttpStatus.BAD_REQUEST.value());
		} else {
			findOne.getLikedBy().add(p.getName());
			if (findOne.getLikes() == 0) {
				findOne.setLikes(1L);
			} else {
				long likes = findOne.getLikes() +1;
				findOne.setLikes(likes);
			}
			videos.save(findOne);
			response.setStatus(HttpStatus.OK.value());
		}
		 return;
	}
	
	@RequestMapping(value="/video/{id}", method=RequestMethod.GET)
	public @ResponseBody Video getVideoById(@PathVariable("id") long id, HttpServletResponse response) {
		if (videos.findOne(id) == null) {
			 response.setStatus(HttpStatus.NOT_FOUND.value()); 
			 return null;
		} else { 
			return videos.findOne(id);
		}
	}
	
	@RequestMapping(value="/video/{id}/unlike", method=RequestMethod.POST)
	public @ResponseBody void unlikeVideo(@PathVariable("id")  long id , Principal p, HttpServletResponse response) {
		if (videos.findOne(id) == null) {
			 response.setStatus(HttpStatus.NOT_FOUND.value()); 
			 return;
		} 
		Video findOne = videos.findOne(id);
		if (findOne.getLikedBy().contains(p.getName())) {
			findOne.getLikedBy().remove(p.getName());
			if (findOne.getLikes() > 0) {
				long likes = findOne.getLikes();
				likes-=1;
				findOne.setLikes(likes);
			}
			videos.save(findOne);
			response.setStatus(HttpStatus.OK.value());
		} else {
			response.setStatus(HttpStatus.BAD_REQUEST.value());
		}
		return;
	}
	
	@RequestMapping(value="/video/search/findByName", method=RequestMethod.GET)
	public @ResponseBody Collection<Video> findByTitle(@RequestParam("title") String name, HttpServletResponse response) {
		List<Video> responseList = videos.findByName(name);
		if (responseList != null && responseList.size() > 0) {
			return responseList;
		} else {
			return null;
		}
	}
	
	@RequestMapping(value="/video/search/findByDurationLessThan", method=RequestMethod.GET)
	public @ResponseBody Collection<Video> findByDurationLessThan(@RequestParam("duration") long duration, HttpServletResponse response) {
		List<Video> responseList = videos.findByDurationLessThan(duration);
		if (responseList != null && responseList.size() > 0) {
			return responseList;
		} else {
			return null;
		}
	}
}
