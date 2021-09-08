import csv
import importlib
ca = importlib.import_module("ca")

class Node:
    def __init__(self, id, name, type):
        self.name = name
        self.id = id
        self.children = []
        self.type = type
        self.parent = None

    def add_child(self, child):
        self.children.append(child)
        child.parent = self

def walk(node, depth):
    print(depth * "  " + node.name + " " + node.id + " " + ca.data_types[node.type])
    for child in node.children:
        walk(child, depth+1)


nodes = []
nodes_map = {}

with open('csv/2020-07-03-ca_metadata_elements.csv', newline='') as csvfile:
    elements = csv.reader(csvfile, delimiter=',', quotechar='"')
    for row in elements:
        # 0: ﻿element_id
        # 1: parent_id
        # 2: list_id
        # 3: element_code
        # 4: documentation_url
        # 5: datatype
        # 6: settings
        # 7: rank
        # 8: hier_left
        # 9: hier_right
        # 10: hier_element_id
        node = Node(row[0], row[3], int(row[5]))
        nodes.append(node)
        nodes_map[node.id] = node

        if (row[1] != ""):
            nodes_map[row[1]].add_child(node)

for node in nodes:
    if node.parent == None:
        walk(node, 0)
        print("")
