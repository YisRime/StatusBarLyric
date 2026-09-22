![ic_home_background.webp](ic_home_background.webp)

<div align="center">

![Release Download](https://img.shields.io/github/downloads/577fkj/StatusBarLyric/total?style=flat-square)
![Release Download](https://img.shields.io/github/downloads/Xposed-Modules-Repo/statusbar.lyric/total?style=flat-square)
[![Release Version](https://img.shields.io/github/v/release/577fkj/StatusBarLyric?style=flat-square)](https://github.com/577fkj/StatusBarLyric/releases/latest)  
[![GitHub Star](https://img.shields.io/github/stars/577fkj/StatusBarLyric?style=flat-square)](https://github.com/577fkj/StatusBarLyric/stargazers)
[![GitHub Fork](https://img.shields.io/github/forks/577fkj/StatusBarLyric?style=flat-square)](https://github.com/577fkj/StatusBarLyric/network/members)
![GitHub Repo size](https://img.shields.io/github/repo-size/577fkj/StatusBarLyric?style=flat-square&color=3cb371)
[![GitHub license](https://img.shields.io/github/license/577fkj/StatusBarLyric?style=flat-square)](LICENSE)
[![GitHub Repo Languages](https://img.shields.io/github/languages/top/577fkj/StatusBarLyric?style=flat-square)](https://github.com/577fkj/StatusBarLyric/search?l=java)  
[![Build Status](https://img.shields.io/endpoint.svg?url=https%3A%2F%2Factions-badge.atrox.dev%2F577fkj%2FStatusBarLyric%2Fbadge%3Fref%3Dmain&style=flat)](https://actions-badge.atrox.dev/577fkj/StatusBarLyric/goto?ref=main)
[![Telegram](https://img.shields.io/badge/电报群-StatusBatLyric-blue.svg?style=flat-square&color=12b7f5)](https://t.me/StatusBatLyric)

</div>

## 版本与作者

- 版本：8.0.0（versionCode 800）
- 原项目作者：[577fkj](https://github.com/577fkj/StatusBarLyric) / [Block-Network](https://github.com/Block-Network/StatusBarLyric)
- 本分支维护与 libxposed 适配：[Yis_Rime](https://github.com/YisRime)

本版将模块入口由 `assets/xposed_init` 迁移至 `META-INF/xposed/java_init.list`，Hook 层改用 libxposed API（`io.github.libxposed:api:101`、EzXHelper 3.x），配置读写改由 libxposed RemotePreferences 承载，不再依赖全局可读的 XML 偏好文件；首次与框架建立连接时把配置从旧的 `shared_prefs` 一次性导入，随后删除旧文件。因不再走 legacy Xposed API，本模块要求宿主框架实现 libxposed 规范，LSPosed 1.x 与 LSPatch 等仅支持旧 API 的框架无法加载。歌词数据仍通过 Lyric Getter 的广播获取，两者需同时启用。

### 这是什么软件？

- 这是一个Xposed模块，需框架支持 libxposed 规范（LSPosed 2.x 及以上）
- 用于在状态栏显示歌词，支持各式各样的自定义样式
- 理论支持 __所有__ 官方以及部分修改系统

### 下载

- [Releases](https://github.com/577fkj/StatusBarLyric/releases)
- [Beta](https://github.com/577fkj/StatusBarLyric/actions/workflows/Android.yml)
- [Canary](https://github.com/577fkj/StatusBarLyric/actions/workflows/Android_Dev.yml)

---

## 帮助翻译

[![Crowdin](https://badges.crowdin.net/statusbarlyric/localized.svg)](https://crowdin.com/project/statusbarlyric)

---

[使用教程](https://blog.xiaowine.cc/posts/8e64/)

[想做贡献？](doc/CONTRIBUTING.md)

[奉献者](https://github.com/Block-Network/StatusBarLyric/graphs/contributors)

[EULA](doc/EULA.md)

[GNU General Public License v3.0](LICENSE)

## Star History

[![Star History Chart](https://api.star-history.com/svg?repos=Block-Network/StatusBarLyric&type=Date)](https://star-history.com/#Block-Network/StatusBarLyric&Date)