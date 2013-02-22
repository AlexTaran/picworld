#!/usr/bin/python

from Tkinter import *

class LevelEditor:
  def __init__(self, master):
    frame = Frame(master)
    frame.pack()
    #packX = Pack(master, )
    frameX = Frame(frame, anchor = W);
    Label(frameX, text = "Size X:", anchor = W).pack(side = LEFT)
    self.fieldSizeX = Entry(frameX, width = 5)
    self.fieldSizeX.insert(0, "10")
    self.fieldSizeX.pack(side = LEFT)
    frameX.pack()
    frameY = Frame(frame, anchor = W)
    Label(frameY, text = "Size Y:", anchor = W).pack(side = LEFT)
    self.fieldSizeY = Entry(frameY, width = 5)
    self.fieldSizeY.insert(0, "15")
    self.fieldSizeY.pack(side = LEFT)
    frameY.pack()
    self.applyButton = Button(frame, text = "Apply Size", command = self.applySize)
    self.applyButton.pack()

    self.frameGrid = Frame(frame)
    for i in range(10):
      for j in range(10):
        Button(self.frameGrid, text = "Cell [%d,%d]" %(i, j)).grid(row=i, column=j)
    self.frameGrid.pack()
    self.button = Button(frame, text = "QUIT", fg = "red", command = frame.quit)
    self.button.pack(side = LEFT)
    self.hi_there = Button(frame, text = "Hello", command = self.say_hi)
    self.hi_there.pack(side = LEFT)

  def applySize(self):
    sizeX = int(self.fieldSizeX.get())
    sizeY = int(self.fieldSizeY.get())
    
    

  def say_hi(self):
    print "hi there, everyone!"

def main():
  root = Tk()
  app = LevelEditor(root)
  root.mainloop()

if __name__ == "__main__":
  main()
