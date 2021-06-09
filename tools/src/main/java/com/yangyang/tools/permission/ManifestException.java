package com.yangyang.tools.permission;

/**
 * @author yangyang
 * @date :2021/4/1
 * @desc: 动态申请的权限没有在清单文件中注册会抛出的异常
 */
class ManifestException extends RuntimeException {

    ManifestException() {
        // 清单文件中没有注册任何权限
        super("No permissions are registered in the manifest file");
    }

    ManifestException(String permission) {
        // 申请的危险权限没有在清单文件中注册
        super(permission + ": Permissions are not registered in the manifest file");
    }
}
