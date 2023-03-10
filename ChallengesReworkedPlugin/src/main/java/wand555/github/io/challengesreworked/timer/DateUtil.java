package wand555.github.io.challengesreworked.timer;

import java.time.Duration;
import java.util.regex.Pattern;

public class DateUtil {

    public static String formatDuration(long time) {
        Duration duration = Duration.ofSeconds(time);
        long seconds = duration.getSeconds();
        long absSeconds = Math.abs(seconds);
        String positive = String.format(
                "%d:%02d:%02d",
                absSeconds / 3600,
                (absSeconds % 3600) / 60,
                absSeconds % 60);
        return seconds < 0 ? "-" + positive : positive;
    }

    public static String formatNoHourDuration(long time) {
        Duration duration = Duration.ofSeconds(time);
        long seconds = duration.getSeconds();
        long absSeconds = Math.abs(seconds);
        String positive = String.format(
                "%02d:%02d",
                (absSeconds % 3600) / 60,
                absSeconds % 60);
        return seconds < 0 ? "-" + positive : positive;
    }

    public static long getSecondsFromFormattedDuration(String duration) {
        if(duration==null)
            return 0;
        try{

            Pattern patternDuration = Pattern.compile("\\d+(?::\\d+){0,2}");

            int hours = 0;
            int minutes = 0;
            int seconds = 0;
            if(patternDuration.matcher(duration).matches()){
                String[] tokens = duration.split(":");
                if(tokens.length==1){
                    seconds = Integer.parseInt(tokens[0]);
                    if(seconds >= 60) return 0;
                }else if(tokens.length == 2){
                    minutes = Integer.parseInt(tokens[0]);
                    if(minutes >= 60) return 0;
                    seconds = Integer.parseInt(tokens[1]);
                    if(seconds >= 60) return 0;
                }else{
                    hours = Integer.parseInt(tokens[0]);
                    if(hours >= 100) return 0;
                    minutes = Integer.parseInt(tokens[1]);
                    if(minutes >= 60) return 0;
                    seconds = Integer.parseInt(tokens[2]);
                    if(seconds >= 60) return 0;
                }

                return 3600 * hours + 60 * minutes + seconds;
            }else
                return 0;

        }catch (NumberFormatException ignored){
            return 0;
        }

    }
}
