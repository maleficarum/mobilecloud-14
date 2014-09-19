package org.magnum.dataup;

import org.magnum.dataup.model.Video
import org.magnum.dataup.model.VideoStatus;
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes
import org.springframework.web.multipart.MultipartFile
import org.springframework.web.multipart.support.StandardMultipartHttpServletRequest
import retrofit.client.Response
import retrofit.http.Multipart
import retrofit.http.Streaming

import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse;
import java.util.concurrent.atomic.AtomicLong;


/**
 * Date: 28/07/14 14:48
 *
 * @author Oscar I. Hernandez
 */
@Controller
public class MyController {

    def videos = [:];
    private static final AtomicLong currentId = new AtomicLong(0L);
    VideoFileManager manager = VideoFileManager.get();

    @RequestMapping(value = "/video", method = RequestMethod.POST)
    public @ResponseBody org.magnum.dataup.model.Video postVideo(@RequestBody org.magnum.dataup.model.Video v){
        if(!v.getId()) {
            v.setId(currentId.incrementAndGet())
        }

        if(!v.getDataUrl()) {
            v.setDataUrl(getDataUrl(v.getId()))
        }
        videos[v.getId()] = v
        v
    }

    @RequestMapping(value = "/video", method = RequestMethod.GET)
    public @ResponseBody List getVideo(){
        return videos.values().asList();
    }

    @Multipart
    @RequestMapping(value = "/video/{id}/data", method = RequestMethod.POST)
    public @ResponseBody VideoStatus setVideo(@PathVariable("id") long id, @RequestParam("data") MultipartFile file, HttpServletRequest request, HttpServletResponse response) {
        Video video = videos[id]
        def videoStatus = null

        if(!video) {
            response.status = 404
        } else {
            manager.saveVideoData(video, file.getInputStream())
            videoStatus = new VideoStatus()
            videoStatus.setState(VideoStatus.VideoState.READY)
        }

        videoStatus
    }

    @Streaming
    @RequestMapping(value = "/video/{id}/data", method = RequestMethod.GET)
    public Response getData(@PathVariable("id") long id, HttpServletRequest request, HttpServletResponse response) {
        Video video = videos[id]

        if(video) {
            manager.copyVideoData(video, response.getOutputStream())
            new Response(request.getRequestURL().toString(), 200, "Video ${id} found", [], null)
        } else {
            new Response(request.getRequestURL().toString(), 404, "Video ${id} not found", [], null)
        }
    }

    private String getDataUrl(long videoId){
        String url = getUrlBaseForLocalServer() + "/video/" + videoId + "/data";
        return url;
    }

    private String getUrlBaseForLocalServer() {
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
        String base = "http://"+request.getServerName() + ((request.getServerPort() != 80) ? ":"+request.getServerPort() : "");
        return base;
    }

}

