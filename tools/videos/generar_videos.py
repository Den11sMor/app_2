"""Genera las dos microclases originales con Pillow, voz local y FFmpeg.

Uso: python generar_videos.py CARPETA_CON_JSON_Y_WAV CARPETA_DESTINO
Dependencias de producción: pillow, imageio-ffmpeg. No se usan en la app.
"""
import json
import math
from pathlib import Path
import subprocess
import sys
import wave

from PIL import Image, ImageDraw, ImageFont
import imageio_ffmpeg

W, H, FPS = 960, 540, 20
BG, INK, PURPLE = '#FCF9FC', '#241A30', '#6750A4'
LILAC, MUTED, BROWN = '#EADDFF', '#51475D', '#593528'
FONT_DIR = Path('C:/Windows/Fonts')


def font(size, bold=False):
    return ImageFont.truetype(str(FONT_DIR / ('seguisb.ttf' if bold else 'segoeui.ttf')), size)


def wrap(draw, text, fnt, width):
    lines, current = [], ''
    for word in text.split():
        candidate = (current + ' ' + word).strip()
        if current and draw.textlength(candidate, font=fnt) > width:
            lines.append(current)
            current = word
        else:
            current = candidate
    if current:
        lines.append(current)
    return lines


def paragraph(draw, text, xy, fnt, width, fill=INK, spacing=5):
    x, y = xy
    for line in wrap(draw, text, fnt, width):
        draw.text((x, y), line, font=fnt, fill=fill)
        y += fnt.size + spacing
    return y


def chocolate_bar(d, x, y, size=1, alpha=1):
    bw, bh = 122*size, 81*size
    d.rounded_rectangle((x, y, x+bw, y+bh), radius=10*size, fill=BROWN)
    for col in range(3):
        for row in range(2):
            a, b = x+8*size+col*36*size, y+8*size+row*35*size
            d.rounded_rectangle((a, b, a+29*size, b+28*size), radius=4*size, fill='#86543B', outline='#A97452', width=2)


def bowl(d, phase, chunks=False):
    d.pieslice((642, 224, 878, 392), 0, 180, fill='#B8A7CF', outline=PURPLE, width=3)
    d.ellipse((642, 253, 878, 314), fill=BROWN, outline='#41251D', width=3)
    for k in range(3):
        a = phase*math.tau + k*math.tau/3
        x, y = 760 + 56*math.cos(a), 282 + 13*math.sin(a)
        d.arc((x-24,y-8,x+24,y+8), 10, 165, fill='#B47B51', width=3)
    angle = -0.8 + .3*math.sin(phase*math.tau)
    end = (760+105*math.sin(angle), 277-105*math.cos(angle))
    d.line((760, 282, *end), fill='#A56C43', width=12)
    if chunks:
        for k in range(3):
            y = 174 + ((phase + k/3) % 1)*105
            x = 742 + 32*math.sin(k+phase*4)
            d.rounded_rectangle((x,y,x+18,y+18), radius=3, fill='#946342')


def cake(d, decorated=0, coated=True):
    d.ellipse((629, 355, 896, 403), fill='#D0BCD9')
    d.rounded_rectangle((655, 226, 870, 367), radius=12, fill='#EAC4A9' if not coated else '#F6E8F8')
    d.rectangle((655, 274, 870, 289), fill='#B17F64' if not coated else '#E0C7E9')
    d.ellipse((655, 194, 870, 258), fill='#FDF1E3' if not coated else '#FFF9FF', outline='#D9BEDF', width=3)
    d.arc((655, 335, 870, 386), 0, 180, fill='#AD89BE', width=3)
    if decorated:
        count = int(decorated*12)
        for k in range(count):
            a = k*math.tau/12
            x, y = 761+83*math.cos(a), 226+21*math.sin(a)
            rosette(d, x, y, 11)


def rosette(d, x, y, radius):
    for k in range(7):
        a = k*math.tau/7
        xx, yy = x+radius*.48*math.cos(a), y+radius*.48*math.sin(a)
        d.ellipse((xx-radius*.48, yy-radius*.48, xx+radius*.48, yy+radius*.48), fill='#9974BB', outline='#76559A')
    d.ellipse((x-3, y-3, x+3, y+3), fill='#E3D0EF')


def bag(d, x, y):
    d.polygon([(x-52,y-92),(x+45,y-92),(x+16,y-5),(x-8,y+6)], fill='#CBB2E3', outline=PURPLE, width=3)
    d.polygon([(x-8,y+6),(x+16,y-5),(x+7,y+25)], fill='#89939F', outline='#58616B', width=2)
    d.line((x-51,y-91,x+44,y-91), fill=PURPLE, width=8)


def illustration(d, kind, phase, progress):
    d.rounded_rectangle((606, 137, 911, 412), radius=30, fill='#F2ECF8')
    if kind == 'chocolate':
        chocolate_bar(d, 650, 218, 1.2)
        d.ellipse((802, 179, 866, 243), fill=PURPLE)
        d.line([(819,211),(830,222),(850,198)],fill='white',width=5)
    elif kind in ('melt', 'seed'):
        bowl(d, phase, kind == 'seed')
        if kind == 'melt':
            # Movimiento del nivel del termómetro; no representa una temperatura universal.
            d.rounded_rectangle((870, 173, 885, 282), radius=7, fill='white', outline=PURPLE, width=2)
            fill_y = 255-35*(.5+.5*math.sin(phase*math.tau))
            d.rectangle((876,fill_y,879,279), fill='#C86F69')
    elif kind == 'test':
        d.rounded_rectangle((640,203,878,350), radius=13, fill='#FFF9EB', outline='#D2C2AA', width=2)
        d.rounded_rectangle((679,245,679+150*min(1,progress*2),281), radius=12, fill=BROWN)
        if progress > .45:
            d.ellipse((808,304,867,363), fill=PURPLE)
            d.line([(822,332),(833,343),(853,319)],fill='white',width=5)
    elif kind == 'mold':
        d.rounded_rectangle((646,190,878,362), radius=18, fill='#C4B5D9', outline=PURPLE, width=3)
        for row in range(2):
            for col in range(3):
                x, y = 664+col*69, 209+row*71
                idx = row*3+col
                d.rounded_rectangle((x,y,x+56,y+53),radius=10,fill=BROWN if progress*7>idx else '#E6DDEC')
    elif kind in ('cake','coat','finish'):
        cake(d, min(1,progress*1.2) if kind=='finish' else 0, kind!='cake')
        if kind == 'coat':
            y = 239+44*math.sin(phase*math.tau)
            d.rounded_rectangle((849,y,870,y+70),radius=4,fill='#ACB3C0',outline='#657080',width=2)
            d.line((859,y-30,859,y),fill=PURPLE,width=12)
    elif kind == 'bag':
        bag(d,760,289+4*math.sin(phase*math.tau))
        d.text((670,348),'Presión estable',font=font(22,True),fill=PURPLE)
    elif kind == 'pipe':
        d.rounded_rectangle((632,281,890,380),radius=14,fill='#FFFBF2',outline='#D6C5AC',width=2)
        for k in range(min(5,int(progress*6))):
            rosette(d,659+k*46,330,12)
        bag(d,660+min(4.1,progress*5)*46,290)


def base_frame(scene, lesson, idx):
    im = Image.new('RGB', (W,H), BG)
    d = ImageDraw.Draw(im)
    d.rounded_rectangle((30,24,930,68),radius=18,fill=PURPLE)
    d.text((50,31),'MASTER MARTINI  /  AULA EN VIDEO',font=font(22,True),fill='white')
    d.text((835,32),f'{idx+1:02d} / 05',font=font(21,True),fill='white')
    d.text((48,94),scene['label'],font=font(21,True),fill=PURPLE)
    y = paragraph(d,scene['title'],(48,137),font(39,True),525,spacing=4)
    y = max(246,y+23)
    for line in scene['bullets']:
        d.ellipse((49,y+9,58,y+18),fill=PURPLE)
        y = paragraph(d,line,(73,y),font(25),499,fill=MUTED,spacing=3)+13
    d.rounded_rectangle((30,435,930,511),radius=18,fill='#EBE2F3')
    return im


def render_scene(scene, lesson, idx, seconds, ffmpeg, target):
    base = base_frame(scene,lesson,idx)
    # Texto de apoyo resumido en dos líneas, visible incluso sin audio.
    captions = []
    words = scene['speech'].split()
    for k in range(0,len(words),15):
        captions.append(' '.join(words[k:k+15]))
    frames = math.ceil(seconds*FPS)
    command=[ffmpeg,'-y','-loglevel','error','-f','rawvideo','-pix_fmt','rgb24','-s',f'{W}x{H}',
             '-r',str(FPS),'-i','-','-an','-c:v','libx264','-preset','veryfast','-crf','24',
             '-profile:v','baseline','-level','3.1','-pix_fmt','yuv420p','-movflags','+faststart',str(target)]
    process=subprocess.Popen(command,stdin=subprocess.PIPE)
    for frame in range(frames):
        t=frame/FPS
        progress=frame/max(1,frames-1)
        im=base.copy()
        d=ImageDraw.Draw(im)
        illustration(d,scene['kind'],(t%3)/3,progress)
        paragraph(d,captions[min(len(captions)-1,int(progress*len(captions)))],(49,446),font(23),864,spacing=4)
        d.rounded_rectangle((30,522,930,529),radius=3,fill='#DED0EA')
        d.rounded_rectangle((30,522,30+900*progress,529),radius=3,fill=PURPLE)
        process.stdin.write(im.tobytes())
        if frame==frames//2:
            im.save(target.with_suffix('.png'))
    process.stdin.close()
    if process.wait()!=0:
        raise RuntimeError(f'No se pudo codificar {target}')


def main():
    workspace, output = map(Path,sys.argv[1:3])
    output.mkdir(parents=True,exist_ok=True)
    lessons=json.loads((workspace/'lessons.json').read_text(encoding='utf-8-sig'))
    ffmpeg=imageio_ffmpeg.get_ffmpeg_exe()
    metadata={}
    for key,lesson in lessons.items():
        muxed=[]
        duration=0
        for idx,scene in enumerate(lesson['scenes']):
            audio=workspace/f'{key}_{idx}.wav'
            with wave.open(str(audio),'rb') as wav:
                seconds=wav.getnframes()/wav.getframerate()+1.0
            seconds=math.ceil(seconds*FPS)/FPS
            silent=workspace/f'{key}_{idx}_silent.mp4'
            render_scene(scene,lesson,idx,seconds,ffmpeg,silent)
            clip=workspace/f'{key}_{idx}_clip.mp4'
            subprocess.run([ffmpeg,'-y','-loglevel','error','-i',str(silent),'-i',str(audio),
                '-c:v','copy','-af','apad','-t',str(seconds),'-c:a','aac','-b:a','80k','-ac','1','-ar','44100',
                '-movflags','+faststart',str(clip)],check=True)
            muxed.append(clip)
            duration+=seconds
            print(f'{key}: escena {idx+1} lista ({seconds:.1f}s)',flush=True)
        listing=workspace/f'{key}_concat.txt'
        listing.write_text('\n'.join("file '"+str(p.resolve()).replace('\\','/')+"'" for p in muxed),encoding='utf-8')
        destination=output/f'{key}.mp4'
        subprocess.run([ffmpeg,'-y','-loglevel','error','-f','concat','-safe','0','-i',str(listing),
            '-c','copy','-movflags','+faststart',str(destination)],check=True)
        # Comprobar la decodificación completa de audio y video.
        subprocess.run([ffmpeg,'-v','error','-i',str(destination),'-f','null','-'],check=True)
        metadata[key]={'titulo':lesson['title'],'duracion_segundos':round(duration,2),
                       'ancho':W,'alto':H,'fps':FPS,'bytes':destination.stat().st_size}
    (workspace/'metadata.json').write_text(json.dumps(metadata,ensure_ascii=False,indent=2),encoding='utf-8')
    sheet=Image.new('RGB',(960,810),'white')
    for row,key in enumerate(lessons):
        for col,idx in enumerate((0,2,4)):
            p=Image.open(workspace/f'{key}_{idx}_silent.png').resize((480,270))
            # Dos columnas, tres filas: cada lección ocupa una columna.
            sheet.paste(p,(row*480,col*270))
    sheet.save(workspace/'preview.png')
    print(json.dumps(metadata,ensure_ascii=False),flush=True)


if __name__=='__main__':
    main()
