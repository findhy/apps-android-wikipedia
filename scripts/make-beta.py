#!/usr/bin/env python
"""
Script that helps move the app from org.wikipedia to org.wikipedia.beta

Does the following things:
    - Move package from org.wikipedia to org.wikipedia.beta
    - Move folders to accommodate new packages
    - Replace all instances of string 'org.wikipedia' to 'org.wikipedia.beta'
    - Setup app to use beta icon
    - Bump versionCode and versionName
    - Make a new commit on a new branch

Requires the python module 'sh' to run. Ensure you have a clean working
directory before running as well.
"""
import sh
import os
import re
import time

PATH_PREFIX = os.path.abspath(os.path.join(os.path.dirname(__file__), ".."))


def p(*path_fragments):
    """
    Combine the path fragments with PATH_PREFIX as a base, and return
    the new full path
    """
    return os.path.join(PATH_PREFIX, *path_fragments)


def get_beta_name():
    """
    Returns name used for beta naming, based on current date
    """
    return '2.0-beta-%s' % time.strftime('%Y-%m-%d')


def git_mv_dir(dir_path):
    """
    Performs git mv from main package to a .beta subpackage
    """
    # Need to do the move in two forms, since we can not
    # move a directory to be its own subdirectory in one step
    sh.git.mv(
        p(dir_path, 'src/main/java/org/wikipedia'),
        p(dir_path, 'src/main/java/org/beta')
    )

    sh.mkdir('-p', p(dir_path, 'src/main/java/org/wikipedia'))

    sh.git.mv(
        p(dir_path, 'src/main/java/org/beta'),
        p(dir_path, 'src/main/java/org/wikipedia')
    )


def transform_file(file_path, *funcs):
    """
    Transforms the file given in file_path by passing it
    serially through all the functions in *func and then
    writing it back out to file_path
    """
    f = open(file_path, 'r+')
    data = f.read()
    f.seek(0)
    for func in funcs:
        data = func(data)
    f.write(data)
    f.close()
    print file_path


def replace_packagenames(data):
    """
    Utility function to replace all non-beta package names
    with beta package names
    """
    return data.replace('org.wikipedia', 'org.wikipedia.beta')


def change_icon(data):
    """
    Utility function to replace alpha launcher icon with
    beta launcher icon
    """
    return data.replace("launcher_alpha", "launcher_beta")

versionCode_regex = re.compile(r'android:versionCode="(\d+)"', re.MULTILINE)
versionName_regex = re.compile(r'android:versionName="([^"]+)"', re.MULTILINE)


def set_version(data):
    """
    Utility function to set new versionCode and versionName
    """
    new_version_name = get_beta_name()
    version_code = int(versionCode_regex.search(data).groups()[0])

    data = versionCode_regex.sub(
        'android:versionCode="%d"' % (version_code + 1),
        data
    )
    data = versionName_regex.sub(
        'android:versionName="%s"' % new_version_name,
        data
    )
    return data


def transform_project(dir_path):
    """
    Performs all necessary transformations for a particular project
    """
    git_mv_dir(dir_path)
    for root, dirs, files in os.walk(p(dir_path, 'src/main/java/org/wikipedia/beta')):
        for file_name in files:
            file_path = os.path.join(root, file_name)
            transform_file(file_path, replace_packagenames)

    for root, dirs, files in os.walk(p(dir_path, 'res')):
        for file_name in files:
            if file_name.endswith('.xml'):
                file_path = os.path.join(root, file_name)
                transform_file(file_path, replace_packagenames)

    transform_file(p(dir_path, 'AndroidManifest.xml'), replace_packagenames, set_version, change_icon)

if __name__ == '__main__':
    sh.git.checkout('-b', 'betas/%s' % get_beta_name())
    transform_project('wikipedia')
    transform_project('wikipedia-it')
    sh.cd(PATH_PREFIX)
    sh.git.add('-u')
    sh.git.commit('-m', 'Make release %s' % get_beta_name())
