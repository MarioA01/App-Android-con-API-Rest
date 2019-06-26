const express = require("express");
const router = express.Router();
const mongoose = require("mongoose");
const multer = require('multer');
const crypto = require('crypto');
const bodyParser = require('body-parser');

const storage = multer.diskStorage({
  destination: function(req, file, cb) {
    cb(null, './uploads/');
  },
  filename: function(req, file, cb) {
    cb(null, Date.now() + file.originalname);
  }
});

const fileFilter = (req, file, cb) => {
  // reject a file
  if (file.mimetype === 'image/jpeg' || file.mimetype === 'image/png') {
    cb(null, true);
  } else {
    cb(null, false);
  }
};

const upload = multer({
  storage: storage,
  limits: {
    fileSize: 1024 * 1024 * 5
  },
  fileFilter: fileFilter
});

const genRandomString = function(length)
{
  return crypto.randomBytes(Math.ceil(length/2))
  .toString('hex') /* convert to hexa format*/
  .slice(0,length);
}

const sha512 = function(password, salt)
{
  const hash = crypto.createHmac('sha512', salt);
  hash.update(password);
  const value = hash.digest('hex');
  return {
    salt:salt,
    passwordHash:value
  };
}

function saltHashPassword(userPassword)
{
  const salt = genRandomString(16);
  const passwordData = sha512(userPassword,salt);
  return passwordData;
}

function checkHashPassword(userPassword,salt)
{
  const passwordData = sha512(userPassword,salt);
  return passwordData;
}

const Report = require("../models/reportsModel");
const User = require("../models/userModel");

router.get("/reports/", (req, res, next) => {
  Report.find()
    .select("title descripcion date_time _id productImage")
    .exec()
    .then(docs => {
      const response = {
        count: docs.length,
        reports: docs.map(doc => {
          return {
            title: doc.title,
            descripcion: doc.descripcion,
            productImage: doc.productImage,
            _id: doc._id,
            date_time: doc.date_time,
            request: {
              type: "GET",
              url: "http://localhost:3000/API/reports/" + doc._id
            }
          };
        })
      };
      //   if (docs.length >= 0) {
      res.status(200).json(response);
      //   } else {
      //       res.status(404).json({
      //           message: 'No entries found'
      //       });
      //   }
    })
    .catch(err => {
      console.log(err);
      res.status(500).json({
        error: err
      });
    });
});

router.post("/reports/", upload.single('productImage'), (req, res, next) => {
  const report = new Report({
    _id: new mongoose.Types.ObjectId(),
    title: req.body.title,
    descripcion: req.body.descripcion,
    productImage: "http://localhost:3000/" + req.file.path 
  });
  report.save().then(result => {
      console.log(result);
      res.status(201).json({
        message: "Created report successfully",
        createdProduct: {
            title: result.title,
            descripcion: result.descripcion,
            productImage: result.productImage,
            _id: result._id,
            request: {
                type: 'GET',
                url: "http://localhost:3000/API/reports/" + result._id
            }
        }
      });
    })
    .catch(err => {
      console.log(err);
      res.status(500).json({
        error: err
      });
    });
});

router.get("/reports/:reportId", (req, res, next) => {
  const id = req.params.reportId;
  Report.findById(id)
    .select('title descripcion date_time _id productImage')
    .exec()
    .then(doc => {
      console.log("From database", doc);
      if (doc) {
        res.status(200).json({
            report: doc,
            request: {
                type: 'GET',
                url: 'http://localhost:3000/API/reports'
            }
        });
      } else {
        res
          .status(404)
          .json({ message: "No valid entry found for provided ID" });
      }
    })
    .catch(err => {
      console.log(err);
      res.status(500).json({ error: err });
    });
});

router.put("/reports/:reportId", upload.single('productImage'), (req, res, next) => {
  const id = req.params.reportId;
  Report.findById(id).update({
    title: req.body.title,
    descripcion: req.body.descripcion,
    productImage: req.file.path}
  ).exec()
    .then(result => {
      res.status(200).json({
          message: 'Report updated',
          request: {
              type: 'GET',
              url: 'http://localhost:3000/API/reports/' + id
          }
      });
    })
    .catch(err => {
      console.log(err);
      res.status(500).json({
        error: err
      });
    });
});

router.delete("/reports/:reportId", (req, res, next) => {
  const id = req.params.reportId;
  Report.remove({ _id: id })
    .exec()
    .then(result => {
      res.status(200).json({
          message: 'Report deleted',
          request: {
              type: 'POST',
              url: 'http://localhost:3000/API/reports',
              body: { title: 'String', descripcion: 'String' }
          }
      });
    })
    .catch(err => {
      console.log(err);
      res.status(500).json({
        error: err
      });
    });
});

router.post("/register/", (req, res, next) => {
  const plaint_password = req.body.password;
  const hash_data = saltHashPassword(plaint_password);
  const password = hash_data.passwordHash;
  const salt = hash_data.salt;
  const user = new User({
    _id: new mongoose.Types.ObjectId(),
    name: req.body.name,
    email: req.body.email,
    password: password,
    salt: salt,
  });
  User.find({'email':req.body.email}).count(function(err,number){
    if(number != 0){
      res.json('El correo ya esta registrado.');
      console.log('El correo ya esta registrado.');
    }else{
      user.save().then(result => {
        console.log(result);
        res.status(201).json({
          message: "Usuario creado exitosamente.",
          createdUser: {
              name: result.name,
              email: result.email,
              password: result.password,
              salt: salt,
              _id: result._id,
              request: {
                  type: 'GET',
                  url: "http://localhost:3000/API/register/" + result._id
              }
          }
        });
      })
      .catch(err => {
        console.log(err);
        res.status(500).json({
          error: err
        });
      });
    }
  })
});

router.post("/login/", (req, res, next) => {
  const email = req.body.email;
  const userPassword = req.body.password;
  User.find({'email':req.body.email}).count(function(err,number){
    if(number == 0){
      res.json('El correo no existe.');
      console.log('El correo no existe.');
    }else{
      User.findOne({'email':email},function(err,user){
        const salt = user.salt; // Get salt from user
        const hashed_password = checkHashPassword(userPassword,salt).passwordHash;
        const encrypted_password = user.password; //Get password from user
        if(hashed_password == encrypted_password)
        {
          res.json('Ingreso exitoso.');
          console.log('Ingreso exitoso.');
        }else{
          res.json('Contraseña incorrecta.');
          console.log('Contraseña incorrecta.');
        }
      })
    }
  })
});

module.exports = router;
