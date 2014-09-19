package org.magnum.mobilecloud.video

import org.magnum.mobilecloud.video.repository.Video
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestMethod
import org.springframework.web.bind.annotation.ResponseBody

import java.util.concurrent.atomic.AtomicLong;

/**
 * Date: 19/09/14 01:36
 * @author Oscar I. Hernandez
 */
@Controller
class MyController {

    def videos = [:];
    private static final AtomicLong currentId = new AtomicLong(0L);

    @RequestMapping(value = "/video", method = RequestMethod.POST)
    public @ResponseBody Video postVideo(@RequestBody Video v){
        if(!v.getId()) {
            v.setId(currentId.incrementAndGet())
        }

        videos[v.getId()] = v

        v
    }

    @RequestMapping(value = "/video", method = RequestMethod.GET)
    public @ResponseBody List getVideo(){
        return videos.values().asList();
    }

}
