project('mpv_jni', ['cpp'],
        version : '0.1.0',
        default_options : ['cpp_std=c++20', 'fribidi:docs=false', 'fribidi:tests=false']
)

#jni = dependency('jni', include_type : 'system', required : false)

#frbidi_subproject = subproject('fribidi', default_options : ['docs=false', 'tests=false'])
#frbidi_dep = frbidi_subproject.get_variable('libfribidi_dep')

libass_subproject = subproject('libass', default_options : ['default_library=static', 'c_args=--enable-pic', 'require-system-font-provider=false'])
libass_dep = libass_subproject.get_variable('libass_dep')

#libass = declare_dependency(
#    dependencies : [frbidi_dep, libass_dep]
#)

libplacebo_proj = subproject('libplacebo', default_options : ['vulkan=disabled', 'demos=false'])
libplacebo_dep = libplacebo_proj.get_variable('libplacebo')

zlib_dep = dependency('zlib', fallback : ['zlib_dep'])
freetype2_dep = dependency('freetype2', fallback : ['freetype_dep'])

ffmpeg = subproject('ffmpeg', default_options : ['default_library=shared', 'c_args=--enable-pic', 'c_link_args=-shared', 'cpp_link_args=-shared',
                                                 'programs=disabled', 'muxers=disabled', 'devices=disabled', 'encoders=disabled',
                                                 'vulkan=disabled', 'tests=disabled', 'gpl=enabled'])
libavcodec = ffmpeg.get_variable('libavcodec_dep')
libavfilter = ffmpeg.get_variable('libavfilter_dep')
libavformat = ffmpeg.get_variable('libavformat_dep')
libavutil = ffmpeg.get_variable('libavutil_dep')
libswresample = ffmpeg.get_variable('libswresample_dep')
libswscale = ffmpeg.get_variable('libswscale_dep')

libmpv_proj = subproject('mpv', default_options : ['default_library=shared', 'libmpv=true', 'cplayer=false',
                                                   'lua=disabled', 'iconv=disabled', 'manpage-build=disabled',
                                                   'gl=enabled', 'x11=disabled'])
libmpv = libmpv_proj.get_variable('libmpv_dep')

dependencies = [zlib_dep,
                freetype2_dep,
                libplacebo_dep,
                libass_dep,
                libavcodec,
                libavfilter,
                libavformat,
                libavutil,
                libswresample,
                libswscale,
                libmpv
]

mpv = declare_dependency(
    dependencies : dependencies
)

includes = ['include', 'include/mpv', 'include/jni']

target = target_machine.system()

if target == 'windows'
    includes += 'include/jni/win32'
elif target == 'linux'
    includes += 'include/jni/linux'
endif

mpv_jni = shared_library(
    'mpv_jni',
    'src/jni_utils.cpp',
    'src/client.cpp',
    'src/property.cpp',
    'src/event.cpp',
    'src/render.cpp',
    'src/render_gl.cpp',
    'src/stream_cb.cpp',
    include_directories : include_directories(includes),
    dependencies : [mpv],
)