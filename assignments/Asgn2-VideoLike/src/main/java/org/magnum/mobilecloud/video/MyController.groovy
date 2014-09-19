package org.magnum.mobilecloud.video

import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestMethod
import org.springframework.web.bind.annotation.ResponseBody

/**
 * Date: 19/09/14 01:36
 * @author Oscar I. Hernandez
 */
@Controller
class MyController {

    def videos = [:];

    @RequestMapping(value = "/video", method = RequestMethod.GET)
    public @ResponseBody List getVideo(){
        return videos.values().asList();
    }

}
