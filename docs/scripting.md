## 状态机已添加下列常量：
| 常量名                  | 说明                      | 详细说明                               |
|----------------------|-------------------------|------------------------------------|
|`GUNSMITHLIB_INSTALLED`| 如果安装本模组则为`true`，否则为`nil` | 可用于检测是否安装本模组，避免脚本在没有安装本模组的时候报错     |
|`GUNSMITHLIB_MAJOR_VERSION`| GunsmithLib的版本号的第一位     |                                    |
|`GUNSMITHLIB_MINOR_VERSION`| GunsmithLib的版本号的第二位     |                                    |
|`GUNSMITHLIB_PATCH_VERSION`| GunsmithLib的版本号的第三位     |                                    |
|`GUNSMITHLIB_INPUT_COOLDOWN_START`| 破盾（开始冷却）时触发的状态名 | 值为 `"gunsmithlib:cooldown_start"` |

## 双机共享扩展 API
| 函数名                                | 说明           | 详细说明   |
|------------------------------------|--------------|-------|
| `gunsmith_getCooldownSeconds()`    | 获取当前物品的冷却时间  |单位为秒|
| `gunsmith_getCooldownPercent()`    | 当前物品的冷却时间百分比 |0 为冷却完毕可以使用，1 为刚开始冷却|

示例：
```lua
local function runInspectAnimation(context)
    if (GUNSMITHLIB_INSTALLED ~= nil) then
        print(context:gunsmith_getCooldownPercent())
    end
end
```