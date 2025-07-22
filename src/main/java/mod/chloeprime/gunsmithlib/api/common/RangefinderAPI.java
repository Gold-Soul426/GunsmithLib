package mod.chloeprime.gunsmithlib.api.common;

/**
 * 测距 API
 * 可以获得预估射程
 *
 * @since 4.4.0
 */
@SuppressWarnings("unused")
public interface RangefinderAPI {
    /**
     * 获取这把武器预估的射程。
     * 可用于在状态机中控制激光武器的激光块的长度。
     *
     * @return 这把武器预估的射程
     */
    double gunsmith_getEstimatedRange();

    /**
     * 获取这把武器在指定穿透数量下预估的射程。
     * 可用于在状态机中控制激光武器的激光块的长度。
     *
     * @param pierce 能穿透多少个敌人，1 = 能穿透一个或击中两个敌人
     * @return 这把武器在指定穿透数量下预估的射程
     */
    double gunsmith_getEstimatedRange(int pierce);
}
