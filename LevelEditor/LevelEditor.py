#!/usr/bin/python

from Tkinter import *

class LevelEditor:
  def __init__(self, master):
    self.__master = master
    
    #frame = Frame(master)
    #frame.pack()
    #packX = Pack(master, )
    #frameX = Frame(frame);
    #Label(frameX, text = "Size X:").pack(side = LEFT)

    #self.fieldSizeX = Entry(frameX, width = 5)
    #self.fieldSizeX.insert(0, "10")
    #self.fieldSizeX.pack(side = LEFT)
    #frameX.pack()
    #frameY = Frame(frame)
    #Label(frameY, text = "Size Y:").pack(side = LEFT)
    #self.fieldSizeY = Entry(frameY, width = 5)
    #self.fieldSizeY.insert(0, "15")
    #self.fieldSizeY.pack(side = LEFT)
    #frameY.pack()
    #self.applyButton = Button(frame, text = "Apply Size", command = self.applySize)
    #self.applyButton.pack()

    for i in range(10):
      for j in range(10):
        tile_frame = Frame(master, borderwidth=2, relief=GROOVE)
        tile_frame.grid(row = i, column = j)
        
        Button(root, text = '1').grid(row = 1, column = 1)
        Button(root, text = '2').grid(row = 1, column = 2)
         Button(root, text = '__3__').grid(row = 2, column = 1, columnspan = 2)
        
        height = Label(tile_frame, text='0')
        more =  Button(tile_frame, text='+')
        less =  Button(tile_frame, text='-')
        light = Button(tile_frame, text='OFF')
        
        height.grid(row = 0, column = 0, rowspan = 2)
        more.grid(  row = 0, column = 1)
        less.grid(  row = 1, column = 1)
        light.grid( row = 0, column = 2, rowspan = 2)



    #self.frameGrid.pack()
    #self.button = Button(frame, text = "QUIT", fg = "red", command = frame.quit)
    #self.button.pack(side = LEFT)
    #self.hi_there = Button(frame, text = "Hello", command = self.say_hi)
    #self.hi_there.pack(side = LEFT)

  
  def applySize(self):
    sizeX = int(self.fieldSizeX.get())
    sizeY = int(self.fieldSizeY.get())
    

def main():
  root = Tk()
  app = LevelEditor(root)
  root.mainloop()

if __name__ == "__main__":
  main()
